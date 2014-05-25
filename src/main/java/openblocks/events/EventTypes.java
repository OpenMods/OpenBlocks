package openblocks.events;

import openblocks.common.MapDataManager;
import openmods.network.event.*;

public enum EventTypes implements IEventPacketType {
	MAP_DATA_REQUEST {
		@Override
		public EventPacket createPacket() {
			return new MapDataManager.MapDataRequestEvent();
		}

		@Override
		public PacketDirectionValidator getDirection() {
			return PacketDirectionValidator.C2S;
		}
	},
	MAP_DATA_RESPONSE {
		@Override
		public EventPacket createPacket() {
			return new MapDataManager.MapDataResponseEvent();
		}

		@Override
		public PacketDirectionValidator getDirection() {
			return PacketDirectionValidator.S2C;
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
		public PacketDirectionValidator getDirection() {
			return PacketDirectionValidator.S2C;
		}
	},
	PLAYER_MOVEMENT {
		@Override
		public EventPacket createPacket() {
			return new ElevatorActionEvent();
		}

		@Override
		public PacketDirectionValidator getDirection() {
			return PacketDirectionValidator.C2S;
		}
	},
	STENCIL_CRAFT {
		@Override
		public EventPacket createPacket() {
			return new StencilCraftEvent();
		}

		@Override
		public PacketDirectionValidator getDirection() {
			return PacketDirectionValidator.C2S;
		}
	},
	PLAYER_ACTION {
		@Override
		public EventPacket createPacket() {
			return new PlayerActionEvent();
		}

		@Override
		public PacketDirectionValidator getDirection() {
			return PacketDirectionValidator.C2S;
		}
	};

	@Override
	public boolean isCompressed() {
		return false;
	}

	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public int getId() {
		return ordinal();
	}

	public static void registerTypes() {
		// TODO
		EventPacketManager.INSTANCE.registerEvent(type);
	}
}
