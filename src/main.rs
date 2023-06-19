#![allow(clippy::type_complexity)]

use shakmaty::{Chess, Position as ChessPosition, Piece as ChessPiece, Square};
use valence::prelude::*;
use valence::client::message::SendMessage;
use valence::entity::armor_stand::ArmorStandEntityBundle;

const BOARD_MIN_X: i32 = -4;
const BOARD_MAX_X: i32 = 3;
const BOARD_MIN_Z: i32 = -4;
const BOARD_MAX_Z: i32 = 3;
const BOARD_Y: i32 = 64;

const SPAWN_POS: DVec3 = DVec3::new(
    (BOARD_MIN_X + BOARD_MAX_X) as f64 / 2.0,
    BOARD_Y as f64 + 1.0,
    (BOARD_MIN_Z + BOARD_MAX_Z) as f64 / 2.0,
);

/// 
#[derive(Component)]
struct Piece {
    piece: ChessPiece,
    square: Square,
}

pub fn main() {
    tracing_subscriber::fmt().init();

    App::new()
        .add_plugins(DefaultPlugins)
        .add_startup_system(setup)
        .add_system(init_clients)
        .add_systems((
            despawn_disconnected_clients,
            reset_oob_clients,
        ))
        .run();
}

fn setup(
    mut commands: Commands,
    server: Res<Server>,
    dimensions: ResMut<DimensionTypeRegistry>,
    mut biomes: ResMut<BiomeRegistry>,
) {
    for (_, _, biome) in biomes.iter_mut() {
        biome.effects.grass_color = Some(0x00ff00);
    }

    let mut instance = Instance::new(ident!("overworld"), &dimensions, &biomes, &server);

    for z in -10..10 {
        for x in -10..10 {
            instance.insert_chunk([x, z], Chunk::default());
        }
    }

    for z in BOARD_MIN_Z..=BOARD_MAX_Z {
        for x in BOARD_MIN_X..=BOARD_MAX_X {
            if (x + z) % 2 == 0 {
                instance.set_block([x, BOARD_Y, z], BlockState::POLISHED_DIORITE);
            } else {
                instance.set_block([x, BOARD_Y, z], BlockState::POLISHED_BLACKSTONE);
            }
        }
    }

    // make outside trims
    {
        for z in BOARD_MIN_Z..=BOARD_MAX_Z {
            instance.set_block([BOARD_MIN_X - 1, BOARD_Y, z], BlockState::SPRUCE_PLANKS);
            instance.set_block([BOARD_MAX_X + 1, BOARD_Y, z], BlockState::SPRUCE_PLANKS);
        }

        for x in BOARD_MIN_X..=BOARD_MAX_X {
            instance.set_block([x, BOARD_Y, BOARD_MIN_Z - 1], BlockState::SPRUCE_PLANKS);
            instance.set_block([x, BOARD_Y, BOARD_MAX_Z + 1], BlockState::SPRUCE_PLANKS);
        }
    }

    let instance_id = commands.spawn(instance).id();
    let board = ChessBoard {
        board: Chess::default(),
    };

    commands.insert_resource(board.clone());

    commands.spawn_batch(board.clone().board.board().clone().into_iter().map(move |(pos, piece)| {
        (
            ArmorStandEntityBundle {
                location: Location(instance_id),
                position: Position::new(DVec3::new(
                    (pos.file() as i32 - 4) as f64 + 0.5,
                    BOARD_Y as f64 - 0.5,
                    (pos.rank() as i32 - 4) as f64 + 0.5,
                )),
                ..Default::default()
            },
            Piece {
                piece,
                square: pos,
            }
        )
    }));
}

#[derive(Resource, Clone, Debug, PartialEq, Eq, Hash)]
struct ChessBoard {
    board: Chess,
}

fn init_clients(
    mut clients: Query<(&mut Client, &mut Location, &mut Position), Added<Client>>,
    instances: Query<Entity, With<Instance>>,
) {
    for (mut client, mut loc, mut pos) in &mut clients {
        client.send_chat_message("Welcome to Chess in Minecraft!".color(Color::GOLD));
        client.send_chat_message("");
        client.send_chat_message("This aims to be a fully functional chess client in Minecraft.");
        client.send_chat_message("This is a work in progress, so expect bugs and missing features.");
        client.send_chat_message("If you find any bugs, please report them to the developer.");


        loc.0 = instances.single();
        pos.set(SPAWN_POS);
    }
}

fn reset_oob_clients(
    mut clients: Query<&mut Position, With<Client>>,
) {
    for mut pos in &mut clients {
        if pos.0.y < 60.0 {
            pos.0 = SPAWN_POS;
        }
    }
}