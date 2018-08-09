package com.zbensoft.e.payment.api.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.InterfaceStatisticsService;
import com.zbensoft.e.payment.db.domain.InterfaceStatistics;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/interfaceStatistics")
@RestController
public class InterfaceStatisticsController {
	@Autowired
	InterfaceStatisticsService interfaceStatisticsService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 接口统计数据
	@PreAuthorize("hasRole('R_BI_IS_Q')")
	@ApiOperation(value = "Query InterfaceStatistics，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<InterfaceStatistics>> selectPage(
			@RequestParam(required = false) Integer interfaceType,
			@RequestParam(required = false) String statisticsTimeStart,
			@RequestParam(required = false) String statisticsTimeEnd,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		InterfaceStatistics interfaceStatistics = new InterfaceStatistics();
			interfaceStatistics.setInterfaceType(interfaceType);
		interfaceStatistics.setStatisticsTimeStart(statisticsTimeStart);
		interfaceStatistics.setStatisticsTimeEnd(statisticsTimeEnd);
		List<InterfaceStatistics> list = new ArrayList<InterfaceStatistics>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = interfaceStatisticsService.selectPage(interfaceStatistics);

		} else {
			list = interfaceStatisticsService.selectPage(interfaceStatistics);
		}

		int count = interfaceStatisticsService.count(interfaceStatistics);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<InterfaceStatistics>>(new ArrayList<InterfaceStatistics>(), HttpRestStatus.NOT_FOUND);
		}
		
		long reqSum = 0;
		long reqSuccessSum = 0;
		for(InterfaceStatistics bean : list){
			reqSum+=bean.getRequestNum();
			reqSuccessSum+=bean.getRequestSuccNum();
		}
		list.get(0).setRequestSum(reqSum);
		list.get(0).setRequestSuccSum(reqSuccessSum);
		
		return new ResponseRestEntity<List<InterfaceStatistics>>(list, HttpRestStatus.OK, count, count);
	}
	// 接口统计数据
	//@PreAuthorize("hasRole('R_BI_IS_Q')")
	@ApiOperation(value = "Query InterfaceStatistics，Support paging", notes = "")
	@RequestMapping(value = "/daily", method = RequestMethod.GET)
	public ResponseRestEntity<List<InterfaceStatistics>> selectPageDay(
			@RequestParam(required = false) Integer interfaceType,
			@RequestParam(required = false) String statisticsTimeStart,
			@RequestParam(required = false) String statisticsTimeEnd,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length, @RequestParam(required = false) String type) {
		InterfaceStatistics interfaceStatistics = new InterfaceStatistics();
			interfaceStatistics.setInterfaceType(interfaceType);
		interfaceStatistics.setStatisticsTimeStart(statisticsTimeStart);
		interfaceStatistics.setStatisticsTimeEnd(statisticsTimeEnd);
		List<InterfaceStatistics> list = new ArrayList<InterfaceStatistics>();
		List<InterfaceStatistics> listNew = new ArrayList<InterfaceStatistics>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = interfaceStatisticsService.selectPageDay(interfaceStatistics,type);

		} else {
			list = interfaceStatisticsService.selectPageDay(interfaceStatistics,type);
		}

		int count = interfaceStatisticsService.countDay(interfaceStatistics,type);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<InterfaceStatistics>>(new ArrayList<InterfaceStatistics>(), HttpRestStatus.NOT_FOUND);
		}
		
		for(InterfaceStatistics bean:list){
			bean.setStatisticsTime(bean.getStatisticsTimeDays());
			listNew.add(bean);
		}
		
		return new ResponseRestEntity<List<InterfaceStatistics>>(listNew, HttpRestStatus.OK, count, count);
	}	
	
	
	
	// 主页接口统计
	@ApiOperation(value = "Query Main InterfaceStatistics，Support paging", notes = "")
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public ResponseRestEntity<List<InterfaceStatistics>> selectMainInterface() {
		InterfaceStatistics interfaceStatistics = new InterfaceStatistics();
		List<InterfaceStatistics> list = new ArrayList<InterfaceStatistics>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cBefore = Calendar.getInstance();
		cBefore.add(Calendar.DATE, -4);
		interfaceStatistics.setStatisticsTimeStart(sdf.format(cBefore.getTime()));
		
		Calendar cNow = Calendar.getInstance();
		cNow.add(Calendar.DATE, 0);
		interfaceStatistics.setStatisticsTimeEnd(sdf.format(cNow.getTime()));
		
		list = interfaceStatisticsService.selectPage(interfaceStatistics);

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<InterfaceStatistics>>(new ArrayList<InterfaceStatistics>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<InterfaceStatistics>>(list, HttpRestStatus.OK);
	}

}