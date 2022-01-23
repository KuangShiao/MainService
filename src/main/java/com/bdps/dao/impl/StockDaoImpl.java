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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.bdps.dao.StockDao;
import com.bdps.entity.TblIndustryConfig;
import com.bdps.entity.TblStockBasis;
import com.bdps.entity.TblStockPrice;
import com.bdps.module.StockInfo;

public class StockDaoImpl implements StockDao {

	private static Logger logger = LoggerFactory.getLogger(StockDaoImpl.class);
	
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
	@Override
	public void saveStockBasis(String stockNo, String stockName, String listingDate, String marketNo, String industryNo) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("insert into tblStockBasis (stockNo, stockName, listingDate, marketNo, industryNo)              ").append(System.lineSeparator())
		   .append(" values (:stockNo, :stockName, to_date(:listingDate, 'YYYY/MM/DD'), :marketNo, :industryNo)    ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stockNo", stockNo);
		paramMap.put("stockName", stockName);
		paramMap.put("listingDate", listingDate);
		paramMap.put("marketNo", marketNo);
		paramMap.put("industryNo", industryNo);
		
		logger.info("saveStockBasis sql: {}, paramMap: {}", sql, paramMap);
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
	}
	
	@Override
	public List<TblStockBasis> findStockBasisByMarketNo(String marketNo) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("select stockNo, stockName, listingDate, marketNo, industryNo       ").append(System.lineSeparator())
		   .append("  from tblStockBasis t                                             ").append(System.lineSeparator())
		   .append(" where marketNo = :marketNo                                        ").append(System.lineSeparator())
		   .append("   and not exists (select *                                        ").append(System.lineSeparator())
	       .append("                     from tblStockPrice                            ").append(System.lineSeparator())
		   .append("                    where to_char(opendt, 'YYYYMMDD') = '20220121' ").append(System.lineSeparator())
		   .append("                      and t.stockNo = stockNo                     ").append(System.lineSeparator())
		   .append("                      and sma5 is not null)                        ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("marketNo", marketNo);
		
		logger.info("findStockBasisByMarketNo sql: {}, paramMap: {}", sql, paramMap);
		List<TblStockBasis> list = this.namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(TblStockBasis.class));
		logger.info("Result size: {}", list.size());
		
		return list;
	}
	
	@Override
	public void saveStockPrice(String stockNo, Timestamp openDt, double closePrice, double highPrice, double openPrice, double lowPrice, int volume) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
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
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
	}
	
	@Override
	public List<TblIndustryConfig> findAllIndustryConfig() throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("select industryNo,                    ").append(System.lineSeparator())
		   .append("       industryName,                  ").append(System.lineSeparator())
		   .append("	   createDate,                    ").append(System.lineSeparator())
		   .append("       modifyDate                     ").append(System.lineSeparator())
		   .append("  from tblIndustryConfig              ").append(System.lineSeparator());
		
		logger.info("findAllIndustryConfig sql: {}", sql);
		List<TblIndustryConfig> list = this.namedParameterJdbcTemplate.query(sql.toString(), new HashMap<>(), new BeanPropertyRowMapper<>(TblIndustryConfig.class));
		logger.info("Result size: {}", list.size());
		
		return list;
	}
	
	@Override
	public List<StockInfo> findStockInfo(String stockNoName, String industryNo) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
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
		List<StockInfo> list = this.namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(StockInfo.class));
		logger.debug("Result size: {}", list.size());
		
		return list;
	}
	
	@Override
	public double findSma(String stockNo, Timestamp openDt, int sma) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("select nvl(sum(closePrice), 0) / :sma   ").append(System.lineSeparator())
		   .append("  from (                                ").append(System.lineSeparator())
		   .append("         select closePrice              ").append(System.lineSeparator())
		   .append("           from tblStockPrice           ").append(System.lineSeparator())
		   .append("          where stockNo = :stockNo      ").append(System.lineSeparator())
		   .append("            and openDt <= :openDt       ").append(System.lineSeparator())
		   .append("            and rownum <= :sma          ").append(System.lineSeparator())
		   .append("          order by opendt desc          ").append(System.lineSeparator())
		   .append("        )                               ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stockNo", stockNo);
		paramMap.put("openDt", openDt);
		paramMap.put("sma", sma);
		
		logger.info("findSma sql: {}, paramMap: {}", sql, paramMap);
		double d = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Double.class);
		logger.debug("Result: {}", d);
		
		return d;
	}
	
	@Override 
	public void updateSma(String stockNo, Timestamp openDt, double sma5, double sma10, double sma20, double sma60, double sma120, double sma240) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
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
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
	}
	
	@Override
	public void updateForeignInvestors(List<TblStockPrice> list) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("update tblStockPrice                                    ").append(System.lineSeparator())
		   .append("   set foreignInvestors = :foreignInvestors             ").append(System.lineSeparator())
		   .append(" where stockNo = :stockNo                               ").append(System.lineSeparator())
		   .append("   and openDt = :openDt                                 ").append(System.lineSeparator());
		
		logger.info("updateForeignInvestors sql: {}, list.size: {}", sql, list.size());
		int[] status = this.namedParameterJdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(list.toArray()));
		logger.debug("status: {}", status);
	}
	
	@Override
	public void updateInvestmentTrust(List<TblStockPrice> list) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("update tblStockPrice                                    ").append(System.lineSeparator())
		   .append("   set investmentTrust = :investmentTrust               ").append(System.lineSeparator())
		   .append(" where stockNo = :stockNo                               ").append(System.lineSeparator())
		   .append("   and openDt = :openDt                                 ").append(System.lineSeparator());
		
		logger.info("updateInvestmentTrust sql: {}, list.size: {}", sql, list.size());
		int[] status = this.namedParameterJdbcTemplate.batchUpdate(sql.toString(),  SqlParameterSourceUtils.createBatch(list.toArray()));
		logger.debug("status: {}", status);
	}
	
	@Override
	public void updateDealer(List<TblStockPrice> list) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("update tblStockPrice                                    ").append(System.lineSeparator())
		   .append("   set dealer = :dealer                                 ").append(System.lineSeparator())
		   .append(" where stockNo = :stockNo                               ").append(System.lineSeparator())
		   .append("   and openDt = :openDt                                 ").append(System.lineSeparator());
		
		logger.info("updateDealer sql: {}, list.size: {}", sql, list.size());
		int[] status = this.namedParameterJdbcTemplate.batchUpdate(sql.toString(),  SqlParameterSourceUtils.createBatch(list.toArray()));
		logger.debug("status: {}", status);
	}

	@Override
	public List<StockInfo> findStockByType01() throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("with stockInfo as (                                                                                               ").append(System.lineSeparator())
		   .append("    select nvl(t2.closePrice, 0) as closePrice,                                                                   ").append(System.lineSeparator())
		   .append("           t1.stockNo,                                                                                            ").append(System.lineSeparator())
		   .append("           t1.stockName,                                                                                          ").append(System.lineSeparator())
		   .append("           t1.listingDate,                                                                                        ").append(System.lineSeparator())
		   .append("           t1.marketNo,                                                                                           ").append(System.lineSeparator())
		   .append("           t1.industryNo,                                                                                         ").append(System.lineSeparator())
		   .append("           (select industryName from tblIndustryConfig where industryNo = t1.industryNo) as industryName          ").append(System.lineSeparator())
		   .append("      from tblStockBasis t1                                                                                       ").append(System.lineSeparator())
		   .append("    left join                                                                                                     ").append(System.lineSeparator())
		   .append("           ( select a.stockNo, a.closePrice                                                                       ").append(System.lineSeparator())
		   .append("               from ( select stockNo, openDt, closePrice                                                          ").append(System.lineSeparator())
		   .append("                        from tblStockPrice ) a,                                                                   ").append(System.lineSeparator())
		   .append("                    ( select stockNo, Max(openDt) as openDt                                                       ").append(System.lineSeparator())
		   .append("                        from tblStockPrice                                                                        ").append(System.lineSeparator())
		   .append("                      group by stockNo ) b                                                                        ").append(System.lineSeparator())
		   .append("              where a.stockNo = b.stockNo                                                                         ").append(System.lineSeparator())
		   .append("                and a.openDt = b.openDt ) t2                                                                      ").append(System.lineSeparator())
		   .append("        on t1.stockNo = t2.stockNo                                                                                ").append(System.lineSeparator())
		   .append(")                                                                                                                 ").append(System.lineSeparator())
		   .append("                                                                                                                  ").append(System.lineSeparator())
		   .append("select x.*                                                                                                        ").append(System.lineSeparator())
		   .append("  from stockInfo x,                                                                                               ").append(System.lineSeparator())
		   .append("       ( select stockNo                                                                                           ").append(System.lineSeparator())
		   .append("           from ( select t1.stockNo,                                                                              ").append(System.lineSeparator())
		   .append("                         t1.volume,                                                                               ").append(System.lineSeparator())
		   .append("                         row_number() over (partition by t1.stockNo order by t1.openDt desc) as no                ").append(System.lineSeparator())
		   .append("                    from ( select a.stockNo, a.openDt, a.closePrice, a.volume                                     ").append(System.lineSeparator())
		   .append("                             from ( select stockNo, openDt, closePrice, volume,                                   ").append(System.lineSeparator())
		   .append("                                           row_number() over (partition by stockNo order by openDt desc) as no    ").append(System.lineSeparator())
		   .append("                                      from tblStockPrice ) a                                                      ").append(System.lineSeparator())
		   .append("                            where a.no < 4 ) t1,                                        -- 每筆股票最近三天資料                            ").append(System.lineSeparator())
		   .append("                         ( select a.stockNo, a.openDt, a.sma5, a.sma10, a.sma20, a.sma60                          ").append(System.lineSeparator())
		   .append("                             from ( select stockNo, openDt, sma5, sma10, sma20, sma60,                            ").append(System.lineSeparator())
		   .append("                                           row_number() over (partition by stockNo order by openDt desc) as no    ").append(System.lineSeparator())
		   .append("                                      from tblStockPrice ) a                                                      ").append(System.lineSeparator())
		   .append("                            where a.no < 4 ) t2                                         -- 每筆股票最近三天資料                            ").append(System.lineSeparator())
		   .append("                   where t1.stockNo = t2.stockNo                                                                  ").append(System.lineSeparator())
		   .append("                     and t1.openDt = t2.openDt                                                                    ").append(System.lineSeparator())
		   .append("                     and t1.closePrice < t2.sma5                                                                  ").append(System.lineSeparator())
		   .append("                     and t1.closePrice < t2.sma10                                                                 ").append(System.lineSeparator())
		   .append("                     and t1.closePrice < t2.sma20                                                                 ").append(System.lineSeparator())
		   .append("                     and t1.closePrice < t2.sma60 )                                                               ").append(System.lineSeparator())
		   .append("          where no = 3                                                                  -- 取三天皆符合的股票                                 ").append(System.lineSeparator())
		   .append("            and volume > 5000 * 1000 ) y                                                                          ").append(System.lineSeparator())
		   .append(" where x.stockNo = y.stockNo                                                                                      ").append(System.lineSeparator());
	
		logger.info("findStockByType01 sql: {}", sql);
		List<StockInfo> list = this.namedParameterJdbcTemplate.query(sql.toString(), new HashMap<>(), new BeanPropertyRowMapper<>(StockInfo.class));
		logger.debug("Result size: {}", list.size());

		return list;
	}
	
	@Override
	public List<TblStockPrice> findStockPriceByStockNo(String stockNo) throws Exception {
	
		StringBuilder sql = new StringBuilder();
		sql.append("select stockNo,                                       ").append(System.lineSeparator())
		   .append("       openDt,                                        ").append(System.lineSeparator())
		   .append("       closePrice,                                    ").append(System.lineSeparator())
		   .append("       highPrice,                                     ").append(System.lineSeparator())
		   .append("       openPrice,                                     ").append(System.lineSeparator())
		   .append("       lowPrice,                                      ").append(System.lineSeparator())
		   .append("       volume,                                        ").append(System.lineSeparator())
		   .append("       sma5,                                          ").append(System.lineSeparator())
		   .append("       sma10,                                         ").append(System.lineSeparator())
		   .append("       sma20,                                         ").append(System.lineSeparator())
		   .append("       sma60,                                         ").append(System.lineSeparator())
		   .append("       sma120,                                        ").append(System.lineSeparator())
		   .append("       sma240,                                        ").append(System.lineSeparator())
		   .append("       nvl(foreignInvestors, 0) as foreignInvestors,  ").append(System.lineSeparator())
		   .append("       nvl(investmentTrust, 0) as investmentTrust,    ").append(System.lineSeparator())
		   .append("       nvl(dealer, 0) as dealer,                      ").append(System.lineSeparator())
		   .append("       createDate,                                    ").append(System.lineSeparator())
		   .append("       modifyDate                                     ").append(System.lineSeparator())
		   .append("  from tblStockPrice                                  ").append(System.lineSeparator())
		   .append(" where stockNo = :stockNo                             ").append(System.lineSeparator())
		   .append("   and rownum <= :days                                ").append(System.lineSeparator()) 
		   .append(" order by openDt desc                                 ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stockNo", stockNo);
		paramMap.put("days", 30);
		
		logger.info("findStockPriceByStockNo sql: {}, paramMap: {}", sql, paramMap);
		List<TblStockPrice> list = this.namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(TblStockPrice.class));
		logger.debug("Result size: {}", list.size());
		 
		return list;
	}
	
}
