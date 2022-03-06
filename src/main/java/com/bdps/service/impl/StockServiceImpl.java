package com.bdps.service.impl;

import java.io.File;
import java.io.InputStream;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bdps.dao.StockDao;
import com.bdps.dao.UserDao;
import com.bdps.entity.TblAccountBasis;
import com.bdps.entity.TblIndustryConfig;
import com.bdps.entity.TblStockBasis;
import com.bdps.entity.TblStockPrice;
import com.bdps.module.StockInfo;
import com.bdps.module.StockInventory;
import com.bdps.service.HttpService;
import com.bdps.service.StockService;

public class StockServiceImpl implements StockService {

	private static Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

	@Autowired
	private StockDao stockDao;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private HttpService httpService;

	@Override
	public void printStockPrice() throws Exception {

		DateTime currentDt = new DateTime();
		DateTime closingDt = new DateTime(2021, 12, 1, 15, 0, 0, 0);
		int closingLt = (int) (closingDt.getMillis() / 1000);

		logger.info("findStockPrice: {}", this.findStockPrice("2303", closingLt, closingLt, "TWSE"));

//		this.updateStockBasis();
	}

	private Map<String, Object> findStockPrice(String stockNo, int openingLt, int closingLt, String marketNo) throws Exception {

		String url = "";
		if ("TWSE".equals(marketNo)) { // 上市
			url = "https://query1.finance.yahoo.com/v8/finance/chart/" + stockNo + ".TW?" + "period1=" + openingLt
					+ "&period2=" + closingLt + "&interval=1d&events=history&=hP2rOschxO0";
		} else if ("TPEX".equals(marketNo)) { // 上櫃
			url = "https://query1.finance.yahoo.com/v8/finance/chart/" + stockNo + ".TWO?" + "period1=" + openingLt
					+ "&period2=" + closingLt + "&interval=1d&events=history&=hP2rOschxO0";
		} else {
			throw new Exception("marketNo is invalid");
		}

		JSONObject o = new JSONObject(httpService.httpGet(url));
		JSONObject chart = new JSONObject(o.get("chart").toString());
		JSONObject result = chart.getJSONArray("result").getJSONObject(0);
		JSONObject meta = result.getJSONObject("meta");
		JSONObject quote = result.getJSONObject("indicators").getJSONArray("quote").getJSONObject(0);

		DecimalFormat df = new DecimalFormat("######0.00");
		df.setRoundingMode(RoundingMode.HALF_UP); // 四捨五入
		Map<String, Object> stockPriceMap = new HashMap<>();
		stockPriceMap.put("CLOSEPRICE", df.format(Float.parseFloat(String.valueOf(quote.getJSONArray("close").getDouble(0)))));
		
//		stockPriceMap.put("CLOSEPRICE", df.format(Float.parseFloat(String.valueOf(meta.get("regularMarketPrice")))));
//		if (quote.isEmpty()) {
//			logger.warn("stockNo: {} quote.isEmpty()", stockNo);
//			stockPriceMap.put("HIGHPRICE", stockPriceMap.get("CLOSEPRICE"));
//			stockPriceMap.put("OPENPRICE", stockPriceMap.get("CLOSEPRICE"));
//			stockPriceMap.put("LOWPRICE",  stockPriceMap.get("CLOSEPRICE"));
//			stockPriceMap.put("VOLUME", 0);
//		}
//		else {
			stockPriceMap.put("HIGHPRICE", df.format(Float.parseFloat(String.valueOf(quote.getJSONArray("high").getDouble(0)))));
			stockPriceMap.put("OPENPRICE", df.format(Float.parseFloat(String.valueOf(quote.getJSONArray("open").getDouble(0)))));
			stockPriceMap.put("LOWPRICE", df.format(Float.parseFloat(String.valueOf(quote.getJSONArray("low").getDouble(0)))));
			stockPriceMap.put("VOLUME", df.format(Float.parseFloat(String.valueOf(quote.getJSONArray("volume").getDouble(0)))));
//		}


		return stockPriceMap;
	}

	@Override
	public void saveStockBasis() throws Exception {
		String market = "TWSE";
		InputStream is = getClass().getClassLoader().getResourceAsStream("stock/" + market + ".txt");

		String str = IOUtils.toString(is, StandardCharsets.UTF_8);
		logger.info("{}", str);

		String[] stocks = str.split(System.lineSeparator());
		for (String stock : stocks) {
			String stockNo = stock.split("\t")[0];
			String stockName = stock.split("\t")[1];
//			stockDao.saveStockBasis(stockNo, stockName, market);
		}
	}

	private void updateStockBasis() throws Exception {

		String str = FileUtils.readFileToString(new File("C:\\阿光\\上市清單TPEX.txt"));
		logger.info("{}", str);

		String[] stocks = str.split(System.lineSeparator());
		for (String stock : stocks) {
			String stockNo = stock.split("\t")[0].split("　")[0];
			String listingDate = stock.split("\t")[2];
			String industryNo = stock.split("\t")[3];

//			try {
//				stockDao.updateStockBasisById(stockNo, listingDate, industryNo);	
//			} catch (Exception e) {
//				logger.error("updateStockBasis error: {}", e);
//			}

		}

	}

	@Override
	public void saveStockPrice(DateTime openStartDt, DateTime openEndDt) throws Exception {
		List<String> marketNoLt = Arrays.asList("TWSE", "TPEX");

		for (String marketNo : marketNoLt) {
			List<TblStockBasis> tblStockBasisLt = stockDao.findStockBasisByMarketNo(marketNo);
			
			tblStockBasisLt.parallelStream().forEach(stockBasis -> {
				try {
					int openingLt = (int) (openStartDt.getMillis() / 1000);
					int closingLt = (int) (openEndDt.getMillis() / 1000);
					Map<String, Object> stockPriceMap = this.findStockPrice(stockBasis.getStockNo(), openingLt, closingLt, marketNo);
					double closePrice = Double.parseDouble(stockPriceMap.get("CLOSEPRICE").toString());
					double highPrice = Double.parseDouble(stockPriceMap.get("HIGHPRICE").toString());
					double openPrice = Double.parseDouble(stockPriceMap.get("OPENPRICE").toString());
					double lowPrice = Double.parseDouble(stockPriceMap.get("LOWPRICE").toString());
					double volume = Double.parseDouble(stockPriceMap.get("VOLUME").toString());
					stockDao.saveStockPrice(stockBasis.getStockNo(), new Timestamp(openEndDt.getMillis()), closePrice, highPrice, openPrice, lowPrice, (int) volume);
				} catch (Exception e) {
					logger.error("stockNo: {}, occur error: {}", stockBasis.getStockNo(), e.getMessage());
				}
			});

		}

	}
	
	@Override
	public List<TblIndustryConfig> findAllIndustryConfig() throws Exception {
		return stockDao.findAllIndustryConfig();
	}
	
	@Override
	public List<StockInfo> findStockInfo(String stockNoName, String industryNo) throws Exception {
		return stockDao.findStockInfo(stockNoName, industryNo);
	}
	
	@Override
	public void updateSma(DateTime openDt) throws Exception {
		List<String> marketNoLt = Arrays.asList("TWSE", "TPEX");

		for (String marketNo : marketNoLt) {
			List<TblStockBasis> tblStockBasisLt = stockDao.findStockBasisByMarketNo(marketNo);
			
			tblStockBasisLt.parallelStream().forEach(stockBasis -> {
				try {
					double sma5 = stockDao.findSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), 5);
					double sma10 = stockDao.findSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), 10);
					double sma20 = stockDao.findSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), 20);
					double sma60 = stockDao.findSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), 60);
					double sma120 = stockDao.findSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), 120);
					stockDao.updateSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), sma5, sma10, sma20, sma60, sma120, 0);
				} catch (Exception e) {
					logger.error("stockNo: {}, occur error: {}", stockBasis.getStockNo(), e.getMessage());
					try {
						double sma5 = stockDao.findSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), 5);
						double sma10 = stockDao.findSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), 10);
						double sma20 = stockDao.findSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), 20);
						double sma60 = stockDao.findSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), 60);
						double sma120 = stockDao.findSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), 120);
						stockDao.updateSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), sma5, sma10, sma20, sma60, sma120, 0);
					} catch (Exception e1) {
						
					}
				}
			});

		}
	}
	
	@Override
	public void updateBuyAndSell(DateTime openDt) throws Exception {
		
		// [外資]
		List<TblStockPrice> list = new ArrayList<>();
		TblStockPrice tsp = null;
		int i = 0;
		String url = "https://fubon-ebrokerdj.fbs.com.tw/Z/ZE/ZEE/ZEE.djhtm";
		
        Document document = Jsoup.connect(url).validateTLSCertificates(false).get();
        Elements links = document.getElementsByClass("t01").select("tr td");
        for (Element link : links) {
            
        	
        	if (i % 4 == 1) {
        		String val = link.select("a").attr("href").toString();
        		if (!"".equals(val)) {
        			String stockNo = val.replace("javascript:Link2Stk('", "").replace("')", "");
        			tsp = new TblStockPrice();
        			tsp.setStockNo(stockNo);
        		}
        	}
        	else if (i % 4 == 2) {
        		try {
        			tsp.setForeignInvestors(NumberFormat.getIntegerInstance().parse(link.text()).intValue());
        			list.add(tsp);
        		} catch (Exception e) {
        			
        		}
        	}
        	i++;
        }

        list.stream().forEach(s -> s.setOpenDt(new Timestamp(openDt.getMillis())));
     	try {
    		stockDao.updateForeignInvestors(list);	
    	} catch (Exception e) {
    		logger.error("occur error: {}", e);
    	}
        
        //[投信]
		list = new ArrayList<>();
		tsp = null;
		i = 0;
		url = "https://fubon-ebrokerdj.fbs.com.tw/Z/ZE/ZES/ZES.djhtm";
		
        document = Jsoup.connect(url).validateTLSCertificates(false).get();
        links = document.getElementsByClass("t01").select("tr td");
        for (Element link : links) {
            
        	if (i % 4 == 1) {
        		String val = link.select("a").attr("href").toString();
        		if (!"".equals(val)) {
        			String stockNo = val.replace("javascript:Link2Stk('", "").replace("')", "");
        			tsp = new TblStockPrice();	
        			tsp.setStockNo(stockNo);
        		}
        	}
        	else if (i % 4 == 0) {
        		try {
        			tsp.setInvestmentTrust(NumberFormat.getIntegerInstance().parse(link.text()).intValue());
        			list.add(tsp);	
        		} catch (Exception e) {
        			
        		}
        	}
        	i++;
        }
        
        list.stream().forEach(s -> s.setOpenDt(new Timestamp(openDt.getMillis())));
    	try {
    		stockDao.updateInvestmentTrust(list);	
    	} catch (Exception e) {
    		logger.error("occur error: {}", e);
    	}
        
        // [自營商]
		list = new ArrayList<>();
		tsp = null;
		i = 0;
		url = "https://fubon-ebrokerdj.fbs.com.tw/Z/ZE/ZEF/ZEF.djhtm";
		
        document = Jsoup.connect(url).validateTLSCertificates(false).get();
        links = document.getElementsByClass("t01").select("tr td");
        for (Element link : links) {
            
        	if (i % 4 == 1) {
        		String val = link.select("a").attr("href").toString();
        		if (!"".equals(val)) {
        			String stockNo = val.replace("javascript:Link2Stk('", "").replace("')", "");
        			tsp = new TblStockPrice();	
        			tsp.setStockNo(stockNo.replace("AP", ""));
        		}
        		else {
        			try {
    	        		String temp = link.select("script").get(0).toString().replace("<script language=\"javascript\">\r\n" + 
    	        				"<!--\r\n" + 
    	        				"	GenLink2stk('", "");
    	        		int end = temp.indexOf("','");
    	        		String v = temp.substring(0, end);
    	        		if (!"".equals(v)) {
    	        			String stockNo = v.replace("javascript:Link2Stk('", "").replace("')", "");
    	        			tsp = new TblStockPrice();	
    	        			tsp.setStockNo(stockNo.replace("AS", ""));
    	        		}
        			} catch (Exception e) {
            			
            		}
        		}
        	}
        	else if (i % 4 == 0) {
        		try {
        			tsp.setDealer(NumberFormat.getIntegerInstance().parse(link.text()).intValue());
        			list.add(tsp);	
        		} catch (Exception e) {
        			
        		}
        	}
        	i++;
        }
        
        list.stream().forEach(s -> s.setOpenDt(new Timestamp(openDt.getMillis())));
    	try {
    		stockDao.updateDealer(list);	
    	} catch (Exception e) {
    		logger.error("occur error: {}", e);
    	}
        
	}
	
	@Override
	public List<StockInfo> findStockByTypeNo(String typeNo) throws Exception {
		
		List<String> stockNo = null;

		switch (typeNo) {
			case "type_01":
				 stockNo = stockDao.findStockNoByType01();
				 break;
			case "type_02":
				stockNo = stockDao.findStockNoByType02();
				break;
			case "type_03":
				stockNo = stockDao.findStockNoByType03();
				break;
			default:
				logger.warn("can't find typeNo: {}", typeNo);
				return new ArrayList<>();
		}
		
		return stockDao.findStackInfoByStockNo(stockNo);
	}
	
	@Override
	public List<TblStockPrice> findStockPriceByStockNo(String stockNo) throws Exception {
		return stockDao.findStockPriceByStockNo(stockNo);
	}
	
	@Override
	public void updateBuyAndSellByTxt(DateTime openDt) throws Exception {
		String dt = "20220210";
		InputStream is = getClass().getClassLoader().getResourceAsStream("stock/" + dt + ".txt");

		String str = IOUtils.toString(is, StandardCharsets.UTF_8);
		String[] rowData = str.split(System.lineSeparator());
		for (String row : rowData) {
			String[] data = row.split(",");
			
			String stockNo = data[0];
			String fv = data[1];
			String iv = data[2];
			String dv = data[3];
			
//			System.out.println(stockNo + "@" + fv + "@" + iv + "@" + dv);
			stockDao.updateBuyAndSellByTxt(stockNo, new Timestamp(openDt.getMillis()), fv, iv, dv);
		}
	}
	
	@Override
	public List<StockInventory> stockInventoryQuery(String account) throws Exception {
		return stockDao.stockInventoryQuery(account);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void orderStock(String account, double closePrice, String stockNo, int stockNum) throws Exception {
		
		// 扣 accountBasis.cash
		// 寫入 cash 異動紀錄 tblCashHistory
		int handlingFee = (int) (Math.floor(closePrice * 1000 * stockNum * 0.001425) < 20 ? 20 : 
								Math.floor(closePrice * 1000 * stockNum * 0.001425));
		int costPrice = (int) ((closePrice * 1000 * stockNum) + handlingFee);
		this.spendCash(account, "下單股票", costPrice);
		
		// 寫入 tblStockInventory
		boolean updateStatus = stockDao.insertStockInventory(account, stockNo, stockNum, closePrice, costPrice);
		if (!updateStatus) {
			throw new Exception("更新庫存股票失敗");
		}
		// 寫入 紀錄 tblStockTradeHistory
		updateStatus = stockDao.insertStockTradeHistory(account, stockNo, "下單股票", stockNum, closePrice, costPrice, 0);
		if (!updateStatus) {
			throw new Exception("更新股票交易紀錄");
		}		
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void spendCash(String account, String tradeType, int costPrice) throws Exception {
		
		TblAccountBasis tblAccBasis = userDao.getAccBasis(account);
		int cash = (tblAccBasis != null) ? tblAccBasis.getCash() : 0;
		if (cash < costPrice) {
			logger.error("cash 不足");
			logger.error("cash: {}, costPrice: {}", cash, costPrice);
			throw new Exception("cash 不足");
		}
		
		int remainingCash = cash - costPrice;
		
		logger.info("cash: {}, costPrice: {}, remainingCash: {}", cash, costPrice, remainingCash);
		boolean updateStatus = userDao.updateAccBasisCash(account, remainingCash);
		if (!updateStatus) {
			throw new Exception("更新點數失敗");
		}
		
		updateStatus = userDao.insertCashHistory(account, tradeType, costPrice, remainingCash);
		if (!updateStatus) {
			throw new Exception("更新點數失敗");
		}
	}

}
