package web.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import action.ActionOrchestrator;
import web.AX12Http;

public class StopHandler extends AbstractHandler {

	public StopHandler(AX12Http ax12Http) {
		super(ax12Http);
	}

	@Override
	public void handleRequest(HttpExchange arg0) throws IOException {
		ActionOrchestrator ao = this.ax12Http.getActionOrchestrator();
		if (ao != null) {
			ao.stopPlayingPools();
		}
	}

	@Override
	public String getUrlSuffix() {
		return "stop";
	}
	
}
