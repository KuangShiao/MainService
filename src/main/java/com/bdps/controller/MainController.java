package com.bdps.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bdps.service.UserService;
import com.bdps.util.ExceptionHandler;
import com.bdps.util.Vo;

@RestController
public class MainController {

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
			
			boolean result = userSerivce.verifyUser(acc, pwd);
			
			vo.setCheck(true);
			vo.setData(result);
		} catch (Exception e) {
			vo.setCheck(false);
			vo.setMsg(ExceptionHandler.exceptionAsString(e));
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
			
			boolean result = userSerivce.register(acc, pwd, name);
			
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
}
