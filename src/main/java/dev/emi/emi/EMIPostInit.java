package dev.emi.emi;


import dev.emi.emi.data.EmiData;
import dev.emi.emi.network.*;
import dev.emi.emi.platform.EmiClient;
import dev.emi.emi.platform.EmiMain;
import moddedmite.emi.api.EMIPlayerControllerMP;
import moddedmite.emi.util.MinecraftServerEMI;
import moddedmite.emi.platform.fish.EmiAgnosFish;
import shims.java.com.unascribed.retroemi.RetroEMI;
import shims.java.net.minecraft.network.PacketByteBuf;
import net.minecraft.Minecraft;
import net.minecraft.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class EMIPostInit {

	private static boolean isEMIInit = false;

	public static void initEMI() {
		if (!isEMIInit) {
			InRelauncher.init();
			isEMIInit = true;
		}
	}

	public static final class InRelauncher {

		public static void init() {
			EmiAgnosFish.poke();
			if (!MinecraftServerEMI.getIsServer()) {
				Client.init();
			}
//			else {
//				Server.init();
//			}
			EmiMain.init();
			
			EmiNetwork.initServer((player, packet) -> {
				player.playerNetServerHandler.sendPacketToPlayer(toVanilla(packet));
			});
			
			PacketReader.registerServerPacketReader(EmiNetwork.FILL_RECIPE, FillRecipeC2SPacket::new);
			PacketReader.registerServerPacketReader(EmiNetwork.CREATE_ITEM, CreateItemC2SPacket::new);
			PacketReader.registerServerPacketReader(EmiNetwork.CHESS, EmiChessPacket.C2S::new);
		}
		
		
		/*
                    NetworkRegistry.instance().registerConnectionHandler(new IConnectionHandler() {
                        @Override
                        public void playerLoggedIn(Player var1, NetHandler var2, INetworkManager var3) {
                            if (var1 instanceof EntityPlayerMP esp) {
                                EmiNetwork.sendToClient(esp, new PingS2CPacket());
                            }
                        }

                        @Override public void clientLoggedIn(NetHandler var1, INetworkManager var2, Packet1Login var3) {}
                        @Override public void connectionClosed(INetworkManager var1) {}
                        @Override public String connectionReceived(NetLoginHandler var1, INetworkManager var2) { return null; }
                        @Override public void connectionOpened(NetHandler var1, String var2, int var3, INetworkManager var4) {}
                        @Override public void connectionOpened(NetHandler var1, MinecraftServer var2, INetworkManager var3) {}
                    });
                }
                */
		private static Packet250CustomPayload toVanilla(EmiPacket packet) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			PacketByteBuf buf = PacketByteBuf.out(dos);
			packet.write(buf);
			Packet250CustomPayload pkt = new Packet250CustomPayload(RetroEMI.compactify(packet.getId()), baos.toByteArray());
			return pkt;
		}
		
		public static final class Client {
			
			public static void init() {
				EmiClient.init();
				EmiData.init();
				
				EmiNetwork.initClient(packet -> ((EMIPlayerControllerMP) Minecraft.getMinecraft().playerController).getNetClientHandler().addToSendQueue(toVanilla(packet)));
				PacketReader.registerClientPacketReader(EmiNetwork.PING, PingS2CPacket::new);
				//NYI
				//PacketReader.registerClientPacketReader(EmiNetwork.COMMAND, CommandS2CPacket::new);
				PacketReader.registerClientPacketReader(EmiNetwork.CHESS, EmiChessPacket.S2C::new);
			}
			
		}
		
//		public static final class Server {
//
//			public static void init() {
//				EmiData.init(ResourceReloader::reload);
//			}
//		}
	}
}
