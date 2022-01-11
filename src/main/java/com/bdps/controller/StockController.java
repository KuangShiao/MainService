package com.bdps.controller;

import java.util.Arrays;
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
import com.bdps.module.StockInfo;
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
//			JSONObject json = new JSONObject(body);
//			String acc = json.optString("acc", "");
//			String pwd = json.optString("pwd", "");
			
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
	
	@RequestMapping(value = "/findAllIndustryConfig", produces = "application/json", method = RequestMethod.POST)
	public Vo findAllIndustryConfig(@RequestBody String body) {

		Vo vo = new Vo();

		logger.info("[S]========== findAllIndustryConfig ==========");
		try {
			JSONObject json = new JSONObject(body);
			
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

}
