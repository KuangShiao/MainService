package com.bdps.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bdps.dao.StockDao;
import com.bdps.entity.TblIndustryConfig;
import com.bdps.entity.TblStockBasis;
import com.bdps.module.StockInfo;
import com.bdps.service.HttpService;
import com.bdps.service.StockService;

public class StockServiceImpl implements StockService {

	private static Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

	@Autowired
	private StockDao stockDao;

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
					stockDao.updateSma(stockBasis.getStockNo(), new Timestamp(openDt.getMillis()), sma5, sma10, sma20, sma60, 0, 0);
				} catch (Exception e) {
					logger.error("stockNo: {}, occur error: {}", stockBasis.getStockNo(), e.getMessage());
				}
			});

		}
	}

}
