package thefirstchange.example.com.communicationtext.netty.protocol.response.dongtaiResponse;

import java.util.Vector;

import thefirstchange.example.com.communicationtext.dongtai.DongtaiPCTNum;
import thefirstchange.example.com.communicationtext.netty.protocol.Packet;

import static thefirstchange.example.com.communicationtext.netty.protocol.command.Command.GetUsODtIDs_RESPONSE;

/*
    进入用户资料界面   用户上拉刷新动态的页面  就是加载旧的动态        返回6条动态的id
    //从当前的dongtaiid开始往前找6条以前的
 */
public class GetUsODtIDsResponsePacket extends Packet {

	String ph;
	Vector<DongtaiPCTNum> dongtaiPCTNums;
	
    @Override
    public int getCommand() {

        return GetUsODtIDs_RESPONSE;
    }

	public String getPh() {
		return ph;
	}

	public void setPh(String ph) {
		this.ph = ph;
	}

	public Vector<DongtaiPCTNum> getDongtaiPCTNums() {
		return dongtaiPCTNums;
	}

	public void setDongtaiPCTNums(Vector<DongtaiPCTNum> dongtaiPCTNums) {
		this.dongtaiPCTNums = dongtaiPCTNums;
	}

}
