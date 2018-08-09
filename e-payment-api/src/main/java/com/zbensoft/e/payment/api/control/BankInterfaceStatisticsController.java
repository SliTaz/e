package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
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
import com.zbensoft.e.payment.api.common.StatisticsUtil;
import com.zbensoft.e.payment.api.service.api.BankInfoService;
import com.zbensoft.e.payment.api.service.api.BankInterfaceStatisticsService;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.BankInterfaceStatistics;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/bankInterfaceStatistics")
@RestController
public class BankInterfaceStatisticsController {
	@Autowired
	BankInterfaceStatisticsService bankInterfaceStatisticsService;
	@Autowired
	BankInfoService bankInfoService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 接口统计数据
	@PreAuthorize("hasRole('R_BI_BDT_Q') OR hasRole('R_BI_BMT_Q') OR hasRole('R_BI_BIS_Q')")
	@ApiOperation(value = "Query BankInterfaceStatistics，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<BankInterfaceStatistics>> selectPage(@RequestParam(required = false) Integer interfaceType, @RequestParam(required = false) String bankId,
			@RequestParam(required = false) String statisticsTimeStart, @RequestParam(required = false) String statisticsTimeEnd, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		BankInterfaceStatistics bankInterfaceStatistics = new BankInterfaceStatistics();
		bankInterfaceStatistics.setInterfaceType(interfaceType);
		bankInterfaceStatistics.setBankId(bankId);
		bankInterfaceStatistics.setStatisticsTimeStart(statisticsTimeStart);
		bankInterfaceStatistics.setStatisticsTimeEnd(statisticsTimeEnd);
		List<BankInterfaceStatistics> list = new ArrayList<BankInterfaceStatistics>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = bankInterfaceStatisticsService.selectPage(bankInterfaceStatistics);

		} else {
			list = bankInterfaceStatisticsService.selectPage(bankInterfaceStatistics);
		}

		int count = bankInterfaceStatisticsService.count(bankInterfaceStatistics);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<BankInterfaceStatistics>>(new ArrayList<BankInterfaceStatistics>(), HttpRestStatus.NOT_FOUND);
		}

		long reqSum = 0;
		long reqSuccessSum = 0;
		for (BankInterfaceStatistics bean : list) {
			reqSum += bean.getRequestNum();
			reqSuccessSum += bean.getRequestSuccNum();
		}
		list.get(0).setRequestSum(reqSum);
		list.get(0).setRequestSuccSum(reqSuccessSum);

		List<BankInterfaceStatistics> listNew = new ArrayList<BankInterfaceStatistics>();
		for (BankInterfaceStatistics bean : list) {
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			if (bankInfo != null) {
				bean.setBankName(bankInfo.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<BankInterfaceStatistics>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 接口统计数据
	@PreAuthorize("hasRole('R_BI_BDT_Q') OR hasRole('R_BI_BMT_Q') OR hasRole('R_BI_BIS_Q')")
	@ApiOperation(value = "Query BankInterfaceStatistics，Support paging", notes = "")
	@RequestMapping(value = "/month", method = RequestMethod.GET)
	public ResponseRestEntity<List<BankInterfaceStatistics>> selectPage(@RequestParam(required = false) Integer interfaceType, @RequestParam(required = false) String bankId,
			@RequestParam(required = false) String statisticsTimeStartMonth, @RequestParam(required = false) String statisticsTimeEndMonth, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length, @RequestParam(required = false) String type) {
		BankInterfaceStatistics bankInterfaceStatistics = new BankInterfaceStatistics();
		bankInterfaceStatistics.setInterfaceType(interfaceType);
		bankInterfaceStatistics.setBankId(bankId);
		bankInterfaceStatistics.setStatisticsTimeStartMonth(statisticsTimeStartMonth);
		bankInterfaceStatistics.setStatisticsTimeEndMonth(statisticsTimeEndMonth);
		List<BankInterfaceStatistics> list = new ArrayList<BankInterfaceStatistics>();
		// 分页 start
		if (start != null && length != null) {
			// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */ int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = bankInterfaceStatisticsService.selectPageMonth(bankInterfaceStatistics, type);

		} else {
			list = bankInterfaceStatisticsService.selectPageMonth(bankInterfaceStatistics, type);
		}

		int count = bankInterfaceStatisticsService.countMonth(bankInterfaceStatistics, type);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<BankInterfaceStatistics>>(new ArrayList<BankInterfaceStatistics>(), HttpRestStatus.NOT_FOUND);
		}
		List<BankInterfaceStatistics> listNew = new ArrayList<BankInterfaceStatistics>();
		for (BankInterfaceStatistics bean : list) {
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			if (bankInfo != null) {
				bean.setBankName(bankInfo.getName());
			}
			bean.setStatisticsTime(bean.getStatisticsTimeMonth());

			listNew.add(bean);
		}
		return new ResponseRestEntity<List<BankInterfaceStatistics>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 天统计
	@PreAuthorize("hasRole('R_BI_BDT_Q') OR hasRole('R_BI_BMT_Q') OR hasRole('R_BI_BIS_Q')")
	@ApiOperation(value = "Query BankDailyInterfaceStatistics，Support paging", notes = "")
	@RequestMapping(value = "/daily", method = RequestMethod.GET)
	public ResponseRestEntity<List<BankInterfaceStatistics>> selectBankDailyInter(@RequestParam(required = false) Integer interfaceType, @RequestParam(required = false) String bankId,
			@RequestParam(required = false) String statisticsTimeStart, @RequestParam(required = false) String statisticsTimeEnd, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length, @RequestParam(required = false) String type) {
		BankInterfaceStatistics bankInterfaceStatistics = new BankInterfaceStatistics();
		bankInterfaceStatistics.setInterfaceType(interfaceType);
		bankInterfaceStatistics.setBankId(bankId);
		bankInterfaceStatistics.setStatisticsTimeStart(statisticsTimeStart);
		bankInterfaceStatistics.setStatisticsTimeEnd(statisticsTimeEnd);
		List<BankInterfaceStatistics> list = new ArrayList<BankInterfaceStatistics>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = bankInterfaceStatisticsService.selectPageDay(bankInterfaceStatistics, type);

		} else {
			list = bankInterfaceStatisticsService.selectPageDay(bankInterfaceStatistics, type);
		}

		int count = bankInterfaceStatisticsService.countDay(bankInterfaceStatistics, type);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<BankInterfaceStatistics>>(new ArrayList<BankInterfaceStatistics>(), HttpRestStatus.NOT_FOUND);
		}

		List<BankInterfaceStatistics> listNew = new ArrayList<BankInterfaceStatistics>();
		for (BankInterfaceStatistics bean : list) {

			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			if (bankInfo != null) {
				bean.setBankName(bankInfo.getName());
			}

			bean.setStatisticsTime(bean.getStatisticsTimeDay());

			listNew.add(bean);
		}
		return new ResponseRestEntity<List<BankInterfaceStatistics>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 天统计
	//@PreAuthorize("hasRole('R_BI_BDT_Q') OR hasRole('R_BI_BMT_Q') OR hasRole('R_BI_BIS_Q')")
	@ApiOperation(value = "Query BankDailyInterfaceStatistics，Support paging", notes = "")
	@RequestMapping(value = "/daily/day", method = RequestMethod.GET)
	public ResponseRestEntity<List<BankInterfaceStatistics>> selectBankDailyDay(@RequestParam(required = false) Integer interfaceType, @RequestParam(required = false) String bankId,
			@RequestParam(required = false) String statisticsTimeStart, @RequestParam(required = false) String statisticsTimeEnd, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length, @RequestParam(required = false) String type) {
		BankInterfaceStatistics bankInterfaceStatistics = new BankInterfaceStatistics();
		bankInterfaceStatistics.setInterfaceType(interfaceType);
		bankInterfaceStatistics.setBankId(bankId);
		bankInterfaceStatistics.setStatisticsTimeStart(statisticsTimeStart);
		bankInterfaceStatistics.setStatisticsTimeEnd(statisticsTimeEnd);
		List<BankInterfaceStatistics> list = new ArrayList<BankInterfaceStatistics>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = bankInterfaceStatisticsService.selectPageDailyDay(bankInterfaceStatistics, type);

		} else {
			list = bankInterfaceStatisticsService.selectPageDailyDay(bankInterfaceStatistics, type);
		}

		int count = bankInterfaceStatisticsService.countDailyDay(bankInterfaceStatistics, type);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<BankInterfaceStatistics>>(new ArrayList<BankInterfaceStatistics>(), HttpRestStatus.NOT_FOUND);
		}

		List<BankInterfaceStatistics> listNew = new ArrayList<BankInterfaceStatistics>();
		for (BankInterfaceStatistics bean : list) {
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			if (bankInfo != null) {
				bean.setBankName(bankInfo.getName());
			}
			bean.setStatisticsTime(bean.getStatisticsTimeMainDay());
			listNew.add(bean);
		}
		// 对list进行处理

		return new ResponseRestEntity<List<BankInterfaceStatistics>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 接口统计数据
	@ApiOperation(value = "Query realTimeData，Support paging", notes = "")
	@RequestMapping(value = "/realTimeData", method = RequestMethod.GET)
	public ResponseRestEntity<List<BankInterfaceStatistics>> realTimeData() {
		List<BankInterfaceStatistics> list = new ArrayList<BankInterfaceStatistics>();
		BankInterfaceStatistics bean = new BankInterfaceStatistics();
		bean.setStatisticsReqCount(StatisticsUtil.getStatistics());
		bean.setStatisticsSuccCount(StatisticsUtil.getStatisticsSucc());
		list.add(bean);
		return new ResponseRestEntity<List<BankInterfaceStatistics>>(list, HttpRestStatus.OK);
	}
}
