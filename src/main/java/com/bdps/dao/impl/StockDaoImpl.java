package com.bdps.dao.impl;

import java.sql.Timestamp;
import java.util.Collections;
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
import com.bdps.module.StockInventory;

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
		   .append("                    where to_char(opendt, 'YYYYMMDD') = '20220304' ").append(System.lineSeparator())
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

	public List<StockInfo> findStackInfoByStockNo(List<String> stockNo) throws Exception {
		
		if (stockNo == null || stockNo.size() == 0) {
			logger.info("findStackInfoByStockNo size is empty");
			return Collections.emptyList();
		}
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("select nvl(t2.closePrice, 0) as closePrice,                                                           ").append(System.lineSeparator())        
		   .append("       t1.stockNo,                                                                                    ").append(System.lineSeparator())        
		   .append("       t1.stockName,                                                                                  ").append(System.lineSeparator())        
		   .append("       t1.listingDate,                                                                                ").append(System.lineSeparator())        
		   .append("       t1.marketNo,                                                                                   ").append(System.lineSeparator())        
		   .append("       t1.industryNo,                                                                                 ").append(System.lineSeparator())        
		   .append("       (select industryName from tblIndustryConfig where industryNo = t1.industryNo) as industryName  ").append(System.lineSeparator())        
		   .append("  from tblStockBasis t1                                                                               ").append(System.lineSeparator())        
		   .append("left join                                                                                             ").append(System.lineSeparator())        
		   .append("       ( select a.stockNo, a.closePrice                                                               ").append(System.lineSeparator())        
		   .append("           from ( select stockNo, openDt, closePrice                                                  ").append(System.lineSeparator())        
		   .append("                    from tblStockPrice ) a,                                                           ").append(System.lineSeparator())        
		   .append("                ( select stockNo, Max(openDt) as openDt                                               ").append(System.lineSeparator())        
		   .append("                    from tblStockPrice                                                                ").append(System.lineSeparator())        
		   .append("                  group by stockNo ) b                                                                ").append(System.lineSeparator())        
		   .append("          where a.stockNo = b.stockNo                                                                 ").append(System.lineSeparator())        
		   .append("            and a.openDt = b.openDt ) t2                                                              ").append(System.lineSeparator())        
		   .append("    on t1.stockNo = t2.stockNo                                                                        ").append(System.lineSeparator())
		   .append(" where t1.stockNo in (:stockNo)                                                                       ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stockNo", stockNo);
		
		logger.info("findStackInfoByStockNo sql: {}, paramMap: {}", sql, paramMap);
		List<StockInfo> list = this.namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(StockInfo.class));
		logger.info("Result size: {}", list.size());
		
		return list;
	}
	
	@Override
	public List<String> findStockNoByType01() throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("select stockNo                                                                                           ").append(System.lineSeparator())
           .append("  from ( select t1.stockNo,                                                                              ").append(System.lineSeparator())
           .append("                t1.volume,                                                                               ").append(System.lineSeparator())
           .append("                row_number() over (partition by t1.stockNo order by t1.openDt desc) as no                ").append(System.lineSeparator())
           .append("           from ( select a.stockNo, a.openDt, a.closePrice, a.volume                                     ").append(System.lineSeparator())
           .append("                    from ( select stockNo, openDt, closePrice, volume,                                   ").append(System.lineSeparator())
           .append("                                  row_number() over (partition by stockNo order by openDt desc) as no    ").append(System.lineSeparator())
           .append("                             from tblStockPrice ) a                                                      ").append(System.lineSeparator())
           .append("                   where a.no < 4 ) t1,                                        -- 每筆股票最近三天資料                             ").append(System.lineSeparator())
           .append("                ( select a.stockNo, a.openDt, a.sma5, a.sma10, a.sma20, a.sma60, sma120                  ").append(System.lineSeparator())
           .append("                    from ( select stockNo, openDt, sma5, sma10, sma20, sma60, sma120,                    ").append(System.lineSeparator())
           .append("                                  row_number() over (partition by stockNo order by openDt desc) as no    ").append(System.lineSeparator())
           .append("                             from tblStockPrice ) a                                                      ").append(System.lineSeparator())
           .append("                   where a.no < 4 ) t2                                         -- 每筆股票最近三天資料                             ").append(System.lineSeparator())
           .append("          where t1.stockNo = t2.stockNo                                                                  ").append(System.lineSeparator())
           .append("            and t1.openDt = t2.openDt                                                                    ").append(System.lineSeparator())
           .append("            and t1.closePrice < t2.sma5                                                                  ").append(System.lineSeparator())
           .append("            and t1.closePrice < t2.sma10                                                                 ").append(System.lineSeparator())
           .append("            and t1.closePrice < t2.sma20                                                                 ").append(System.lineSeparator())
           .append("            and t1.closePrice < t2.sma60                                                                 ").append(System.lineSeparator())
           .append("            and t1.closePrice < t2.sma120 )                                                              ").append(System.lineSeparator())
           .append(" where no = 3                                                                  -- 取三天皆符合的股票                                 ").append(System.lineSeparator())
           .append("   and volume > 5000 * 1000                                                                              ").append(System.lineSeparator());
	
		logger.info("findStockNoByType01 sql: {}", sql);
		List<String> list = this.namedParameterJdbcTemplate.queryForList(sql.toString(), new HashMap<>(), String.class);
		logger.debug("Result size: {}", list.size());

		return list;
	}
	
	@Override
	public List<String> findStockNoByType02() throws Exception {
	
		StringBuilder sql = new StringBuilder();
		sql.append("select a.stockNo                                                                                                 ").append(System.lineSeparator())
		   .append("  from ( select t1.stockNo                                                                                       ").append(System.lineSeparator())
		   .append("           from ( select stockNo, openDt, volume, closePrice, sma5, sma10, sma20, sma60, sma120                  ").append(System.lineSeparator())
		   .append("                    from tblStockPrice ) t1,                                                                     ").append(System.lineSeparator())
		   .append("                         ( select *                                                                              ").append(System.lineSeparator())
		   .append("                             from ( select stockNo,                                                              ").append(System.lineSeparator())
		   .append("                                           openDt,                                                               ").append(System.lineSeparator())
		   .append("                                           row_number() over (partition by stockNo order by openDt desc) as no   ").append(System.lineSeparator())
		   .append("                                      from tblStockPrice                                                         ").append(System.lineSeparator())
		   .append("                                    order by openDt desc )                                                       ").append(System.lineSeparator())
		   .append("                            pivot ( max(openDt)                                                                  ").append(System.lineSeparator())
		   .append("                                    for no in ('1' today, '2' yesterday) ) ) t2                                  ").append(System.lineSeparator())
		   .append("                   where t1.stockNo = t2.stockNo                                                                 ").append(System.lineSeparator())
		   .append("                     and t1.openDt = t2.today                                                                    ").append(System.lineSeparator())
		   .append("                     and t1.volume > 5000 * 1000                                                                 ").append(System.lineSeparator())
		   .append("                     and t1.closePrice > t1.sma5                                                                 ").append(System.lineSeparator())
		   .append("                     and t1.closePrice > t1.sma10                                                                ").append(System.lineSeparator())
		   .append("                     and t1.closePrice > t1.sma20                                                                ").append(System.lineSeparator())
		   .append("                     and t1.closePrice > t1.sma60                                                                ").append(System.lineSeparator())
		   .append("                     and t1.closePrice > t1.sma120 ) a,                                                          ").append(System.lineSeparator())
		   .append("                ( select t1.stockNo                                                                              ").append(System.lineSeparator())
		   .append("                    from ( select stockNo, openDt, volume, closePrice, sma5, sma10, sma20, sma60, sma120         ").append(System.lineSeparator())
		   .append("                             from tblStockPrice ) t1,                                                            ").append(System.lineSeparator())
		   .append("                         ( select *                                                                              ").append(System.lineSeparator())
		   .append("                             from ( select stockNo,                                                              ").append(System.lineSeparator())
		   .append("                                           openDt,                                                               ").append(System.lineSeparator())
		   .append("                                           row_number() over (partition by stockNo order by openDt desc) as no   ").append(System.lineSeparator())
		   .append("                                      from tblStockPrice                                                         ").append(System.lineSeparator())
		   .append("                                    order by openDt desc )                                                       ").append(System.lineSeparator())
		   .append("                            pivot ( max(openDt)                                                                  ").append(System.lineSeparator())
		   .append("                                    for no in ('1' today, '2' yesterday) ) ) t2                                  ").append(System.lineSeparator())
		   .append("                   where t1.stockNo = t2.stockNo                                                                 ").append(System.lineSeparator())
		   .append("                     and t1.openDt = t2.yesterday                                                                ").append(System.lineSeparator())
		   .append("                     and t1.volume > 5000 * 1000                                                                 ").append(System.lineSeparator())
		   .append("                     and ( t1.closePrice < t1.sma5 or                                                            ").append(System.lineSeparator())
		   .append("                           t1.closePrice < t1.sma10 or                                                           ").append(System.lineSeparator())
		   .append("                           t1.closePrice < t1.sma20 or                                                           ").append(System.lineSeparator())
		   .append("                           t1.closePrice < t1.sma60 or                                                           ").append(System.lineSeparator())
		   .append("                           t1.closePrice < t1.sma120 ) ) b                                                       ").append(System.lineSeparator())
		   .append(" where a.stockNo = b.stockNo                                                                                     ").append(System.lineSeparator());
		
		logger.info("findStockNoByType02 sql: {}", sql);
		List<String> list = this.namedParameterJdbcTemplate.queryForList(sql.toString(), new HashMap<>(), String.class);
		logger.debug("Result size: {}", list.size());

		return list;
	}
	
	@Override
	public List<String> findStockNoByType03() throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select a.stockNo                                                                                                 ").append(System.lineSeparator())
		   .append("  from ( select t1.stockNo                                                                                       ").append(System.lineSeparator())
		   .append("           from ( select stockNo, openDt, volume, investmentTrust                                                ").append(System.lineSeparator())
		   .append("                    from tblStockPrice ) t1,                                                                     ").append(System.lineSeparator())
		   .append("                         ( select *                                                                              ").append(System.lineSeparator())
		   .append("                             from ( select stockNo,                                                              ").append(System.lineSeparator())
		   .append("                                           openDt,                                                               ").append(System.lineSeparator())
		   .append("                                           row_number() over (partition by stockNo order by openDt desc) as no   ").append(System.lineSeparator())
		   .append("                                      from tblStockPrice                                                         ").append(System.lineSeparator())
		   .append("                                    order by openDt desc )                                                       ").append(System.lineSeparator())
		   .append("                            pivot ( max(openDt)                                                                  ").append(System.lineSeparator())
		   .append("                                    for no in ('1' today, '2' yesterday) ) ) t2                                  ").append(System.lineSeparator())
		   .append("                   where t1.stockNo = t2.stockNo                                                                 ").append(System.lineSeparator())
		   .append("                     and t1.openDt = t2.today                                                                    ").append(System.lineSeparator())
		   .append("                     and t1.volume > 5000 * 1000                                                                 ").append(System.lineSeparator())
		   .append("                     and nvl(t1.investmentTrust, 0) > 0 ) a,                                                     ").append(System.lineSeparator())
		   .append("                ( select t1.stockNo                                                                              ").append(System.lineSeparator())
		   .append("                    from ( select stockNo, openDt, volume, investmentTrust                                       ").append(System.lineSeparator())
		   .append("                             from tblStockPrice ) t1,                                                            ").append(System.lineSeparator())
		   .append("                         ( select *                                                                              ").append(System.lineSeparator())
		   .append("                             from ( select stockNo,                                                              ").append(System.lineSeparator())
		   .append("                                           openDt,                                                               ").append(System.lineSeparator())
		   .append("                                           row_number() over (partition by stockNo order by openDt desc) as no   ").append(System.lineSeparator())
		   .append("                                      from tblStockPrice                                                         ").append(System.lineSeparator())
		   .append("                                    order by openDt desc )                                                       ").append(System.lineSeparator())
		   .append("                            pivot ( max(openDt)                                                                  ").append(System.lineSeparator())
		   .append("                                    for no in ('1' today, '2' yesterday) ) ) t2                                  ").append(System.lineSeparator())
		   .append("                   where t1.stockNo = t2.stockNo                                                                 ").append(System.lineSeparator())
		   .append("                     and t1.openDt = t2.yesterday                                                                ").append(System.lineSeparator())
		   .append("                     and t1.volume > 5000 * 1000                                                                 ").append(System.lineSeparator())
		   .append("                     and nvl(t1.investmentTrust, 0) < 0 ) b                                                      ").append(System.lineSeparator())
		   .append(" where a.stockNo = b.stockNo                                                                                     ").append(System.lineSeparator());
		
		logger.info("findStockNoByType03 sql: {}", sql);
		List<String> list = this.namedParameterJdbcTemplate.queryForList(sql.toString(), new HashMap<>(), String.class);
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
	
	@Override
	public void updateBuyAndSellByTxt(String stockNo, Timestamp openDt, String fv, String iv, String dv) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("update tblStockPrice                ").append(System.lineSeparator()) 
		   .append("   set foreigninvestors = :fv,      ").append(System.lineSeparator()) 
		   .append("       investmenttrust = :iv,       ").append(System.lineSeparator()) 
		   .append("       dealer = :dv                 ").append(System.lineSeparator()) 
		   .append(" where stockNo = :stockNo           ").append(System.lineSeparator()) 
		   .append("   and openDt = :openDt             ");
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stockNo", stockNo);
		paramMap.put("openDt", openDt);
		paramMap.put("fv", fv);
		paramMap.put("iv", iv);
		paramMap.put("dv", dv);
		
		logger.info("updateBuyAndSellByTxt sql: {}, paramMap: {}", sql, paramMap);
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
		
	}

	@Override
	public List<StockInventory> stockInventoryQuery(String account) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select t2.closePrice,                                                                                                                                                            ").append(System.lineSeparator())
		   .append("       t1.stockNo,                                                                                                                                                               ").append(System.lineSeparator())
		   .append("       t1.stockName,                                                                                                                                                             ").append(System.lineSeparator())
		   .append("       t1.stockNum,                                                                                                                                                              ").append(System.lineSeparator())
		   .append("       t1.avgStockPrice,                                                                                                                                                         ").append(System.lineSeparator())
		   .append("       t1.costPrice,                                                                                                                                                             ").append(System.lineSeparator())
		   .append("       case when floor(t2.closePrice * 1000 * t1.stockNum * 0.001425) < 20                                                                                                       ").append(System.lineSeparator())
		   .append("            then (t2.closePrice * 1000 * t1.stockNum) - 20 - floor(t2.closePrice * 1000 * t1.stockNum * 0.003)                                                                   ").append(System.lineSeparator())
		   .append("            else (t2.closePrice * 1000 * t1.stockNum) - floor(t2.closePrice * 1000 * t1.stockNum * 0.001425) - floor(t2.closePrice * 1000 * t1.stockNum * 0.003) end as income   ").append(System.lineSeparator())
		   .append("  from ( select stockNo,                                                                                                                                                         ").append(System.lineSeparator())
		   .append("                ( select stockName from tblStockBasis where stockNo = a.stockNo ) as stockName,                                                                                  ").append(System.lineSeparator())
		   .append("                sum(num) as stockNum,                                                                                                                                            ").append(System.lineSeparator())
		   .append("                sum(stockPrice * num) / sum(num) as avgStockPrice,                                                                                                               ").append(System.lineSeparator())
		   .append("                sum(costPrice) as costPrice                                                                                                                                      ").append(System.lineSeparator())
		   .append("           from tblStockInventory a                                                                                                                                              ").append(System.lineSeparator())
		   .append("          where account = :account                                                                                                                                               ").append(System.lineSeparator())
		   .append("          group by stockNo ) t1,                                                                                                                                                 ").append(System.lineSeparator())
		   .append("       ( select a.stockNo,                                                                                                                                                       ").append(System.lineSeparator())
		   .append("                a.closePrice                                                                                                                                                     ").append(System.lineSeparator())
		   .append("           from tblStockPrice a,                                                                                                                                                 ").append(System.lineSeparator())
		   .append("                ( select stockNo, max(openDt) openDt                                                                                                                             ").append(System.lineSeparator())
		   .append("                    from tblStockPrice                                                                                                                                           ").append(System.lineSeparator())
		   .append("                  group by stockNo ) b                                                                                                                                           ").append(System.lineSeparator())
		   .append("          where a.stockNo = b.stockNo                                                                                                                                            ").append(System.lineSeparator())
		   .append("            and a.openDt = b.openDt ) t2                                                                                                                                         ").append(System.lineSeparator())
		   .append(" where t1.stockNo = t2.stockNo                                                                                                                                                   ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", account);
		
		logger.info("stockInventoryQuery sql: {}, account: {}", sql, paramMap);
		List<StockInventory> list = this.namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(StockInventory.class));
		logger.debug("Result size: {}", list.size());
		 
		return list;
	}
	
	@Override
	public boolean insertStockInventory(String account, String stockNo, int num, double stockPrice, int costPrice) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("insert into tblStockInventory (account, stockNo, buyDt, num, stockPrice, costPrice)       ").append(System.lineSeparator())
		   .append("                    values (:account, :stockNo, sysDate, :num, :stockPrice, :costPrice)   ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", account);
		paramMap.put("stockNo", stockNo);
		paramMap.put("num", num);
		paramMap.put("stockPrice", stockPrice);
		paramMap.put("costPrice", costPrice);
		
		logger.info("insertStockInventory sql: {}, paramMap: {}", sql, paramMap);
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
		
		return status > 0;
	}
	
	@Override
	public boolean insertStockTradeHistory(String account, String stockNo, String tradeType, int num, double stockPrice, int costPrice, int income) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("insert into tblStockTradeHistory (account, stockNo, tradeDt, tradeType, num, stockPrice, costPrice, income)       ").append(System.lineSeparator())
		   .append("                       values (:account, :stockNo, sysDate, :tradeType, :num, :stockPrice, :costPrice, :income)   ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", account);
		paramMap.put("stockNo", stockNo);
		paramMap.put("tradeType", tradeType);
		paramMap.put("num", num);
		paramMap.put("stockPrice", stockPrice);
		paramMap.put("costPrice", costPrice);
		paramMap.put("income", income);
		
		logger.info("tblStockTradeHistory sql: {}, paramMap: {}", sql, paramMap);
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
		
		return status > 0;
	}

}
