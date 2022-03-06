package com.bdps.controller;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bdps.entity.TblIndustryConfig;
import com.bdps.entity.TblStockPrice;
import com.bdps.module.StockInfo;
import com.bdps.module.StockInventory;
import com.bdps.service.StockService;
import com.bdps.util.ExceptionHandler;
import com.bdps.util.Vo;

@RestController
public class StockController {

	private static Logger logger = LoggerFactory.getLogger(StockController.class);
			
	@Autowired
	private StockService stockService;
	
	@RequestMapping(value = "/printStockPrice", produces = "application/json", method = RequestMethod.GET)
	public Vo printStockPrice() {

		Vo vo = new Vo();

		try {			
			stockService.printStockPrice();
			vo.setCheck(true);
		} catch (Exception e) {
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}

		return vo;
	}
	
	@RequestMapping(value = "/saveStockPrice", produces = "application/json", method = RequestMethod.GET)
	public Vo saveStockPrice(@RequestParam("dt") String dt) {

		Vo vo = new Vo();

		logger.info("[S]========== saveStockPrice ==========");
		try {  

			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
			DateTime openDt = dtf.parseDateTime(dt);
			DateTime openStartDt = openDt.plusHours(1);
			DateTime openEndDt = openDt.plusHours(15);
			stockService.saveStockPrice(openStartDt, openEndDt);
			
			DateTimeFormatter dtfOut  = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
			logger.info("openDt: {}", dtfOut.print(openEndDt));

			vo.setCheck(true);
			vo.setData(dtfOut.print(openDt));
		} catch (Exception e) {
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}
		logger.info("[E]========== saveStockPrice ==========");
		
		return vo;
	}
	
	@RequestMapping(value = "/updateSma", produces = "application/json", method = RequestMethod.GET)
	public Vo updateSma(@RequestParam("dt") String dt) {

		Vo vo = new Vo();

		logger.info("[S]========== updateSma ==========");
		try {  

			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
			DateTime openDt = dtf.parseDateTime(dt);
			openDt = openDt.plusHours(15);
			stockService.updateSma(openDt);
			
			DateTimeFormatter dtfOut  = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
			logger.info("openDt: {}", dtfOut.print(openDt));

			vo.setCheck(true);
			vo.setData(dtfOut.print(openDt));
		} catch (Exception e) {
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}
		logger.info("[E]========== updateSma ==========");
		
		return vo;
	}
	
	@RequestMapping(value = "/updateBuyAndSell", produces = "application/json", method = RequestMethod.GET)
	public Vo updateBuyAndSell(@RequestParam("dt") String dt) {

		Vo vo = new Vo();

		logger.info("[S]========== updateBuyAndSell ==========");
		try {  

			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
			DateTime openDt = dtf.parseDateTime(dt);
			openDt = openDt.plusHours(15);
			stockService.updateBuyAndSell(openDt);
			
			DateTimeFormatter dtfOut  = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
			logger.info("openDt: {}", dtfOut.print(openDt));

			vo.setCheck(true);
			vo.setData(dtfOut.print(openDt));
		} catch (Exception e) {
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}
		logger.info("[E]========== updateBuyAndSell ==========");
		
		return vo;
	}
	
	@RequestMapping(value = "/findAllIndustryConfig", produces = "application/json", method = RequestMethod.POST)
	public Vo findAllIndustryConfig(@RequestBody String body) {

		Vo vo = new Vo();

		logger.info("[S]========== findAllIndustryConfig ==========");
		try {
//			JSONObject json = new JSONObject(body);
			
			List<TblIndustryConfig> list = stockService.findAllIndustryConfig();
			vo.setCheck(true);
			vo.setData(list);
		} catch (Exception e) {
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}
		logger.info("E]========== findAllIndustryConfig ==========");

		return vo;
	}
	
	@RequestMapping(value = "/findStockInfo", produces = "application/json", method = RequestMethod.POST)
	public Vo findStockInfo(@RequestBody String body) {

		Vo vo = new Vo();

		logger.info("[S]========== findStockInfo ==========");
		try {
			JSONObject json = new JSONObject(body);
			String stockNoName = json.optString("stockNoName", "");
			String industryNo = json.optString("industryNo", "");
			
			List<StockInfo> list = stockService.findStockInfo(stockNoName, industryNo);
			vo.setCheck(true);
			vo.setData(list);
		} catch (Exception e) {
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}
		logger.info("E]========== findStockInfo ==========");

		return vo;
	}
	
	@RequestMapping(value = "/findStockByTypeNo", produces = "application/json", method = RequestMethod.POST)
	public Vo findStockByTypeNo(@RequestBody String body) {

		Vo vo = new Vo();

		logger.info("[S]========== findStockByTypeNo ==========");
		try {
			JSONObject json = new JSONObject(body);
			String typeNo = json.optString("typeNo", "");
			
			List<StockInfo> list = stockService.findStockByTypeNo(typeNo);
			vo.setCheck(true);
			vo.setData(list);
		} catch (Exception e) {
			logger.error(ExceptionHandler.exceptionAsString(e));
			vo.setCheck(false);
			vo.setMsg("發生未預期的錯誤");
		}
		logger.info("E]========== findStockByTypeNo ==========");

		return vo;
	}
	
	@RequestMapping(value = "/findStockPriceByStockNo", produces = "application/json", method = RequestMethod.POST)
	public Vo findStockDetail(@RequestBody String body) {

		Vo vo = new Vo();

		logger.info("[S]========== findStockPriceByStockNo ==========");
		try {
			JSONObject json = new JSONObject(body);
			String stockNo = json.optString("stockNo", "");
			
			List<TblStockPrice> list = stockService.findStockPriceByStockNo(stockNo);
			vo.setCheck(true);
			vo.setData(list);
		} catch (Exception e) {
			logger.error("findStockPriceByStockNo error: {}", e);
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}
		logger.info("E]========== findStockPriceByStockNo ==========");

		return vo;
	}
	
	@RequestMapping(value = "/updateBuyAndSellByTxt", produces = "application/json", method = RequestMethod.GET)
	public Vo updateBuyAndSellByTxt(@RequestParam("dt") String dt) {

		Vo vo = new Vo();

		logger.info("[S]========== updateBuyAndSellByTxt ==========");
		try {  

			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
			DateTime openDt = dtf.parseDateTime(dt);
			openDt = openDt.plusHours(15);
			stockService.updateBuyAndSellByTxt(openDt);
			
			DateTimeFormatter dtfOut  = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
			logger.info("openDt: {}", dtfOut.print(openDt));

			vo.setCheck(true);
			vo.setData(dtfOut.print(openDt));
		} catch (Exception e) {
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}
		logger.info("[E]========== updateBuyAndSellByTxt ==========");
		
		return vo;
	}
	
	@RequestMapping(value = "/stockInventoryQuery", produces = "application/json", method = RequestMethod.POST)
	public Vo stockInventoryQuery(@RequestBody String body) {

		Vo vo = new Vo();

		logger.info("[S]========== stockInventoryQuery ==========");
		try {
			JSONObject json = new JSONObject(body);
			String account = json.optString("account", "");
			
			List<StockInventory> list = stockService.stockInventoryQuery(account);
			vo.setCheck(true);
			vo.setData(list);
		} catch (Exception e) {
			logger.error("stockInventoryQuery error: {}", e);
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}
		logger.info("E]========== stockInventoryQuery ==========");

		return vo;
	}
	
	@RequestMapping(value = "/orderStock", produces = "application/json", method = RequestMethod.POST)
	public Vo orderStock(@RequestBody String body) {

		Vo vo = new Vo();

		logger.info("[S]========== orderStock ==========");
		try {
			JSONObject json = new JSONObject(body);
			String account = json.optString("account", "");
			double closePrice = json.optDouble("closePrice");
			String stockNo = json.optString("stockNo", "");
			int stockNum = json.optInt("stockNum");
			stockService.orderStock(account, closePrice, stockNo, stockNum);
			vo.setCheck(true);
		} catch (Exception e) {
			logger.error("orderStock error: {}", e);
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}
		logger.info("E]========== orderStock ==========");

		return vo;
	}

}
