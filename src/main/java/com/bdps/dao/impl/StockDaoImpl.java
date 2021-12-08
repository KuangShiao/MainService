package com.bdps.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bdps.dao.StockDao;
import com.bdps.entity.TblIndustryConfig;
import com.bdps.entity.TblStockBasis;
import com.bdps.module.StockInfo;

public class StockDaoImpl implements StockDao {

	private static Logger logger = LoggerFactory.getLogger(StockDaoImpl.class);
	
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
	@Override
	public void saveStockBasis(String stockNo, String stockName, String listingDate, String marketNo, String industryNo) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("insert into tblStockBasis (stockNo, stockName, listingDate, marketNo, industryNo)              ").append(System.lineSeparator())
		   .append(" values (:stockNo, :stockName, to_date(:listingDate, 'YYYY/MM/DD'), :marketNo, :industryNo)    ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stockNo", stockNo);
		paramMap.put("stockName", stockName);
		paramMap.put("listingDate", listingDate);
		paramMap.put("marketNo", marketNo);
		paramMap.put("industryNo", industryNo);
		
		logger.info("saveStockBasis sql: {}, paramMap: {}", sql, paramMap);
		int status = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
	}
	
	@Override
	public List<TblStockBasis> findStockBasisByMarketNo(String marketNo) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select stockNo, stockName, listingDate, marketNo, industryNo       ").append(System.lineSeparator())
		   .append(" from tblStockBasis t                                              ").append(System.lineSeparator())
		   .append(" where marketNo = :marketNo                                        ").append(System.lineSeparator());
//		   .append("   and not exists (select *                                        ").append(System.lineSeparator())
//	       .append("                     from tblStockPrice                            ").append(System.lineSeparator())
//		   .append("                    where to_char(opendt, 'YYYYMMDD') = '20211207' ").append(System.lineSeparator())
//		   .append("                      and t.stockNo = stockNo)                     ").append(System.lineSeparator());
//		   .append("                      and volume <> 0)                             ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("marketNo", marketNo);
		
		logger.info("findStockBasisByMarketNo sql: {}, paramMap: {}", sql, paramMap);
		List<TblStockBasis> list = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(TblStockBasis.class));
		logger.info("Result size: {}", list.size());
		
		return list;
	}
	
	@Override
	public void saveStockPrice(String stockNo, Timestamp openDt, double closePrice, double highPrice, double openPrice, double lowPrice, int volume) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("merge into tblStockPrice a                                                                                                                                                 ").append(System.lineSeparator())
		   .append("using ( select :stockNo as stockNo, :openDt as openDt from dual ) b                                                                                                        ").append(System.lineSeparator())
           .append("   on ( a.stockNo = b.stockNo and a.openDt = b.openDt )                                                                                                                    ").append(System.lineSeparator())
           .append(" when matched then                                                                                                                                                         ").append(System.lineSeparator())
           .append("      update set a.closePrice = :closePrice, a.highPrice = :highPrice, a.openPrice = :openPrice, a.lowPrice = :lowPrice, a.volume = :volume, a.modifyDate = sysDate        ").append(System.lineSeparator())
           .append(" when not matched then                                                                                                                                                     ").append(System.lineSeparator())
           .append("      insert ( stockNo, openDt, closePrice, highPrice, openPrice, lowPrice, volume, createDate, modifyDate )                                                               ").append(System.lineSeparator())
           .append("  	  values ( :stockNo, :openDt, :closePrice, :highPrice, :openPrice, :lowPrice, :volume, sysDate, sysDate )                                                              ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stockNo", stockNo);
		paramMap.put("openDt", openDt);
		paramMap.put("closePrice", closePrice);
		paramMap.put("highPrice", highPrice);
		paramMap.put("openPrice", openPrice);
		paramMap.put("lowPrice", lowPrice);
		paramMap.put("volume", volume);
		
		logger.info("saveStockPrice sql: {}, paramMap: {}", sql, paramMap);
		int status = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
	}
	
	@Override
	public List<TblIndustryConfig> findAllIndustryConfig() throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select industryNo,                    ").append(System.lineSeparator())
		   .append("       industryName,                  ").append(System.lineSeparator())
		   .append("	   createDate,                    ").append(System.lineSeparator())
		   .append("       modifyDate                     ").append(System.lineSeparator())
		   .append("  from tblIndustryConfig              ").append(System.lineSeparator());
		
		logger.info("findAllIndustryConfig sql: {}", sql);
		List<TblIndustryConfig> list = namedParameterJdbcTemplate.query(sql.toString(), new HashMap<>(), new BeanPropertyRowMapper<>(TblIndustryConfig.class));
		logger.info("Result size: {}", list.size());
		
		return list;
	}
	
	@Override
	public List<StockInfo> findStockInfo(String stockNoName, String industryNo) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select nvl(t2.closePrice, 0) as closePrice,                                                            ").append(System.lineSeparator())
		   .append("       t1.stockNo,                                                                                     ").append(System.lineSeparator())   
		   .append("       t1.stockName,                                                                                   ").append(System.lineSeparator())   
		   .append("       t1.listingDate,                                                                                 ").append(System.lineSeparator())   
		   .append("       t1.marketNo,                                                                                    ").append(System.lineSeparator())   
		   .append("       t1.industryNo,                                                                                  ").append(System.lineSeparator())   
		   .append("       (select industryName from tblIndustryConfig where industryNo = t1.industryNo) as industryName   ").append(System.lineSeparator()) 
		   .append("  from tblStockBasis t1                                                                                ").append(System.lineSeparator())
		   .append("left join                                                                                              ").append(System.lineSeparator())
		   .append("       ( select a.stockNo, a.closePrice                                                                ").append(System.lineSeparator())
		   .append("           from ( select stockNo, openDt, closePrice                                                   ").append(System.lineSeparator())
		   .append("                    from tblStockPrice) a,                                                             ").append(System.lineSeparator())
		   .append("                ( select stockNo, Max(openDt) as openDt                                                ").append(System.lineSeparator())
		   .append("                    from tblStockPrice                                                                 ").append(System.lineSeparator())
		   .append("                  group by stockNo) b                                                                  ").append(System.lineSeparator())
		   .append("          where a.stockNo = b.stockNo                                                                  ").append(System.lineSeparator())
		   .append("            and a.openDt = b.openDt ) t2                                                               ").append(System.lineSeparator())
		   .append("    on t1.stockNo = t2.stockNo                                                                         ").append(System.lineSeparator())
		   .append(" where 1 = 1                                                                                           ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		if (StringUtils.isNotBlank(stockNoName)) {
			sql.append(" and (t1.stockNo = :stockNoName or t1.stockName = :stockNoName) ").append(System.lineSeparator());
			paramMap.put("stockNoName", stockNoName);
		}
		if (StringUtils.isNotBlank(industryNo)) {
			sql.append(" and t1.industryNo = :industryNo                                ").append(System.lineSeparator());
			paramMap.put("industryNo", industryNo);
		}
		
		logger.info("findStockInfo sql: {}, paramMap: {}", sql, paramMap);
		List<StockInfo> list = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(StockInfo.class));
		logger.debug("Result size: {}", list.size());
		
		return list;
	}
	
	@Override
	public double findSma(String stockNo, int sma) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select nvl(sum(closePrice), 0) / :sma   ").append(System.lineSeparator())
		   .append("  from (                                ").append(System.lineSeparator())
		   .append("         select closePrice              ").append(System.lineSeparator())
		   .append("           from tblStockPrice           ").append(System.lineSeparator())
		   .append("          where stockNo = :stockNo      ").append(System.lineSeparator())
		   .append("            and rownum <= :sma          ").append(System.lineSeparator())
		   .append("          order by opendt desc          ").append(System.lineSeparator())
		   .append("        )                               ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stockNo", stockNo);
		paramMap.put("sma", sma);
		
		logger.info("findSma sql: {}, paramMap: {}", sql, paramMap);
		double d = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Double.class);
		logger.debug("Result: {}", d);
		
		return d;
	}
	
	@Override 
	public void updateSma(String stockNo, Timestamp openDt, double sma5, double sma10, double sma20, double sma60, double sma120, double sma240) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("update tblStockPrice            ").append(System.lineSeparator())
		   .append("   set sma5 = :sma5,            ").append(System.lineSeparator())
		   .append("       sma10 = :sma10,          ").append(System.lineSeparator())
		   .append("       sma20 = :sma20,          ").append(System.lineSeparator())
		   .append("       sma60 = :sma60,          ").append(System.lineSeparator())
		   .append("       sma120 = :sma120,        ").append(System.lineSeparator())
		   .append("       sma240 = :sma240         ").append(System.lineSeparator())
		   .append(" where stockNo = :stockNo       ").append(System.lineSeparator())
		   .append("   and openDt = :openDt         ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stockNo", stockNo);
		paramMap.put("openDt", openDt);
		paramMap.put("sma5", sma5);
		paramMap.put("sma10", sma10);
		paramMap.put("sma20", sma20);
		paramMap.put("sma60", sma60);
		paramMap.put("sma120", sma120);
		paramMap.put("sma240", sma240);
		
		logger.info("updateSma sql: {}, paramMap: {}", sql, paramMap);
		int status = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
	}

}
