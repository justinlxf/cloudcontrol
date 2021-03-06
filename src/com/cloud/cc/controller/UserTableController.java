package com.cloud.cc.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cloud.cc.service.UserTableService;
import com.cloud.cc.tools.StringUnits;
import com.cloud.cc.vo.UserTable;
import com.cloud.cc.vo.Users;
import com.cloud.cc.vo.model.TableModel;

@Controller
public class UserTableController {

	@Autowired
	private UserTableService userTableService;
	
	/**
	 * 查看当前用户的表
	 * @param request cloudId-项目名称
	 * @return
	 */
	@RequestMapping("/showMyTables")
	@ResponseBody
	public Map<String,Object> showMyTables(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String,Object>();
		String cloudId=request.getParameter("cloudId");
		if(StringUnits.isEmpty(cloudId)) {
			resultMap.put("code", 2);
			return resultMap;
		}
		Users user=(Users)request.getSession().getAttribute("user");
		System.out.println(user.getUserid());
		resultMap.put("code",1);
		resultMap.put("data",userTableService.selectUserTable(user.getUserid(), Integer.parseInt(cloudId)));
		return resultMap;
	}
	
	
	/**
	 * 增加用户表
	 * @param request cloudid-项目Id,tname-表名称,json-json字符串
	 * @return
	 */
	@RequestMapping("/addTable")
	@ResponseBody
	public Map<String,Object> addTables(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String cloudId=request.getParameter("cloudid");
		String tname=request.getParameter("tname");
		String json=request.getParameter("json");
		if(StringUnits.isEmpty(cloudId) || StringUnits.isEmpty(tname) || StringUnits.isEmpty(json)|| !StringUnits.isInteger(cloudId)) {
			resultMap.put("code", 2);
			return resultMap;
		}
		Users user=(Users) request.getSession().getAttribute("user");
		UserTable userTable=new UserTable();
		userTable.setCloudid(Integer.parseInt(cloudId));
		userTable.setCreatetime(new Date());
		userTable.setTablename(tname);
		userTable.setUserid(user.getUserid());
		userTable.setDbtable(StringUnits.genRandomNum());
		TableModel tableModel=JSON.parseObject(json,  new TypeReference<TableModel>() {});
		resultMap.put("code", userTableService.createTable(tableModel, userTable));
		return resultMap;
	}
	
	
	/**
	 * 获取表的所有字段
	 * @param request tableId-表Id
	 * @return
	 */
	@RequestMapping("/getTableField")
	@ResponseBody
	public Map<String,Object> getTableField(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String tableId=request.getParameter("tableId");
		if(StringUnits.isEmpty(tableId) || !StringUnits.isInteger(tableId)) {
			resultMap.put("code", 2);
			return resultMap;
		}
		UserTable userTable=userTableService.selectUserTableByTableId(Integer.parseInt(tableId));
		if(userTable==null) {
			resultMap.put("code", 3);
			return resultMap;
		}
		resultMap.put("code", 1);
		resultMap.put("data", userTableService.selectTableFieldList(userTable.getDbtable()));
		return resultMap;
	}
	
	
	/**
	 * 删除表
	 * @param request tableId-表ID
	 * @return
	 */
	@RequestMapping("/delTable")
	@ResponseBody
	public Map<String,Object> delTable(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String tableId=request.getParameter("tableId");
		if(StringUnits.isEmpty(tableId) || !StringUnits.isInteger(tableId)) {
			resultMap.put("code", 2);
			return resultMap;
		}
		resultMap.put("code",userTableService.delTable(Integer.parseInt(tableId)));
		return resultMap;
	}
}
