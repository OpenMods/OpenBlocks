package openblocks.events;

import openblocks.common.MapDataManager;
import openmods.network.EventIdRanges;
import openmods.network.EventPacket;
import openmods.network.IEventPacketType;
import openmods.network.PacketDirection;

public enum EventTypes implements IEventPacketType {
	MAP_DATA_REQUEST {
		@Override
		public EventPacket createPacket() {
			return new MapDataManager.MapDataRequestEvent();
		}

		@Override
		public PacketDirection getDirection() {
			return PacketDirection.FROM_CLIENT;
		}
	},
	MAP_DATA_RESPONSE {
		@Override
		public EventPacket createPacket() {
			return new MapDataManager.MapDataResponseEvent();
		}

		@Override
		public PacketDirection getDirection() {
			return PacketDirection.TO_CLIENT;
		}

		@Override
		public boolean isCompressed() {
			return true;
		}
	},
	MAP_UPDATES {
		@Override
		public EventPacket createPacket() {
			return new MapDataManager.MapUpdatesEvent();
		}

		@Override
		public PacketDirection getDirection() {
			return PacketDirection.TO_CLIENT;
		}
	},
	PLAYER_MOVEMENT {
		@Override
		public EventPacket createPacket() {
			return new PlayerMovementEvent();
		}

		@Override
		public PacketDirection getDirection() {
			return PacketDirection.FROM_CLIENT;
		}
	},
	STENCIL_CRAFT {
		@Override
		public EventPacket createPacket() {
			return new StencilCraftEvent();
		}

		@Override
		public PacketDirection getDirection() {
			return PacketDirection.FROM_CLIENT;
		}
	},
	PLAYER_ACTION {
		@Override
		public EventPacket createPacket() {
			return new PlayerActionEvent();
		}

		@Override
		public PacketDirection getDirection() {
			return PacketDirection.FROM_CLIENT;
		}
	};

	@Override
	public boolean isCompressed() {
		return false;
	}

	@Override
	public int getId() {
		return EventIdRanges.OPEN_BLOCKS_ID_START + ordinal();
	}

	public static void registerTypes() {
		for (IEventPacketType type : values())
			EventPacket.registerType(type);
	}
}
