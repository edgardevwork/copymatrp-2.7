//
// Created by edgar on 11.06.2025.
//

#ifndef EDGAR_3_0_SPACE_RP_CRPC_H
#define EDGAR_3_0_SPACE_RP_CRPC_H


#include <jni.h>
#include "raknet/NetworkTypes.h"

class CRpc
{
public:
    CRpc();
    static void Packet_CustomRPC(Packet *p);
    static jclass clazzz;

public:
    static bool propv();

    static void vehentry();

    static bool server;

    void sendJsonData(int guiId, _jstring *jsonObject);
private:
    static void ProcessRpcWithTurnState(RakNet::BitStream& bs, uint16_t rpcID);
    static void HandleCheckCashRpc(RakNet::BitStream& bs);
    static void ProcessAntiCheatRpc(RakNet::BitStream& bs);
    static void ProcessCommonGuiRpc(RakNet::BitStream& bs, uint16_t rpcID);
    static void SendCustomRPCPacket(uint8_t rpcID, const std::string& data);
    static void CallJavaOnPacketIncoming(const std::string& jsonString, uint16_t rpcID);
};

#endif //EDGAR_3_0_SPACE_RP_CRPC_H
