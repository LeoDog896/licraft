#![allow(clippy::type_complexity)]

use valence::prelude::*;
use valence::client::message::SendMessage;

const BOARD_MIN_X: i32 = -4;
const BOARD_MAX_X: i32 = 4;
const BOARD_MIN_Z: i32 = -4;
const BOARD_MAX_Z: i32 = 4;
const BOARD_Y: i32 = 64;

const SPAWN_POS: DVec3 = DVec3::new(
    (BOARD_MIN_X + BOARD_MAX_X) as f64 / 2.0,
    BOARD_Y as f64 + 1.0,
    (BOARD_MIN_Z + BOARD_MAX_Z) as f64 / 2.0,
);

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

    commands.spawn(instance);
}

fn init_clients(
    mut clients: Query<(&mut Client, &mut Location, &mut Position), Added<Client>>,
    instances: Query<Entity, With<Instance>>,
) {
    for (mut client, mut loc, mut pos) in &mut clients {
        client.send_chat_message("Lichess in Minecraft!");

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