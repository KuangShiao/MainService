package com.bdps.controller;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.bdps.entity.TblAccountBasis;
import com.bdps.service.UserService;
import com.bdps.util.ExceptionHandler;
import com.bdps.util.Vo;

@RestController
public class MainController {

	private static Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private UserService userSerivce;
	
	@RequestMapping("/")
	public String hello() {
		return "Hello MainService!";
	}

	@RequestMapping(value = "/verifyUser", produces = "application/json", method = RequestMethod.POST)
	public Vo verifyUser(@RequestBody String body) {

		Vo vo = new Vo();

		try {
			JSONObject json = new JSONObject(body);
			String acc = json.optString("acc", "");
			String pwd = json.optString("pwd", "");
			
			TblAccountBasis tblAccBasis = userSerivce.verifyUser(acc, pwd);
			
			vo.setCheck(true);
			vo.setData(tblAccBasis);
		} catch (Exception e) {
			logger.error("verifyUser error: {}", ExceptionHandler.exceptionAsString(e));
			vo.setCheck(false);
			vo.setMsg(e.getMessage());
		}

		return vo;
	}
	
	@RequestMapping(value = "/register", produces = "application/json", method = RequestMethod.POST)
	public Vo register(@RequestBody String body) {

		Vo vo = new Vo();

		try {
			JSONObject json = new JSONObject(body);
			String acc = json.optString("acc", "");
			String pwd = json.optString("pwd", "");
			String name = json.optString("name", "");
			int cash = 500000; // 預設 500,000
			boolean result = userSerivce.register(acc, pwd, name, cash);
			
			vo.setCheck(true);
			vo.setData(result);
		} catch (DuplicateKeyException e) {
			vo.setCheck(false);
			if (e.getMessage().contains("ORA-00001")) {
				vo.setMsg("已存在相同帳號");
			}
			else {
				vo.setMsg(ExceptionHandler.exceptionAsString(e));	
			}
		} catch (Exception e) {
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
		}

		return vo;
	}
	
	@RequestMapping(value = "/findUserProfile", produces = "application/json", method = RequestMethod.POST)
	public Vo findUserProfile(@RequestBody String body) {

		Vo vo = new Vo();

		try {
			JSONObject json = new JSONObject(body);
			String acc = json.optString("acc", "");
			
			TblAccountBasis tblAccBasis = userSerivce.getAccBasis(acc);
			
			vo.setCheck(true);
			vo.setData(tblAccBasis);
		} catch (Exception e) {
			logger.error("findUserProfile error: {}", ExceptionHandler.exceptionAsString(e));
			vo.setCheck(false);
			vo.setMsg(e.getMessage());
		}

		return vo;
	}
	
	@RequestMapping(value = "/uploadUserPhoto", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Vo uploadUserPhoto(HttpServletRequest request) {

		Vo vo = new Vo();

		try {

			MultipartHttpServletRequest mpr = (MultipartHttpServletRequest) request;
			String acc = mpr.getParameter("acc");
			logger.info("account:         {}", acc);
			
			MultipartFile mf = mpr.getMultiFileMap().getFirst("photoImg");
			logger.info("fileName:        {}", mf.getOriginalFilename());
			logger.info("fileSize:        {}", mf.getSize());
			logger.info("fileContentType: {}", mf.getContentType());
//			logger.info("file:            {}", mf.getBytes());

			boolean status = userSerivce.uploadUserPhoto(acc, mf.getBytes());
			vo.setCheck(status);
		} catch (Exception e) {
			logger.error("uploadUserPhoto error: {}", ExceptionHandler.exceptionAsString(e));
			vo.setCheck(false);
			vo.setMsg(e.getMessage());
		}

		return vo;
	}

}
