package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.MerchantActiveStatisticsService;
import com.zbensoft.e.payment.db.domain.MerchantActiveStatistics;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantActiveStatistics")
@RestController
public class MerchantActiveStatisticsController {
	@Autowired
	MerchantActiveStatisticsService activeStatisticsService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	// 查询支付应用，支持分页
	    @PreAuthorize("hasRole('R_BI_SS_Q')")
		@ApiOperation(value = "Query MerchantActiveStatistics，Support paging", notes = "")
		@RequestMapping(value = "", method = RequestMethod.GET)
		public ResponseRestEntity<List<MerchantActiveStatistics>> selectPage(@RequestParam(required = false) String statisticsTime, 
				@RequestParam(required = false) Long totalUserNum, @RequestParam(required = false) Long totalActiveUserNum,
		        @RequestParam(required = false) Long activeUserNum,@RequestParam(required = false) String statisticsTimeStart, 
		        @RequestParam(required = false) String statisticsTimeEnd, 
				@RequestParam(required = false) Long loginUserNum,@RequestParam(required = false) Long loginUserTimes,
				@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
	    	
	    	MerchantActiveStatistics consumerUserActiveStatistics= new MerchantActiveStatistics(); 
	    	
			consumerUserActiveStatistics.setStatisticsTime(statisticsTime);
			consumerUserActiveStatistics.setStatisticsTimeStart(statisticsTimeStart);
			consumerUserActiveStatistics.setStatisticsTimeEnd(statisticsTimeEnd);
			consumerUserActiveStatistics.setTotalUserNum(totalUserNum);
			consumerUserActiveStatistics.setTotalActiveUserNum(totalActiveUserNum);
			consumerUserActiveStatistics.setActiveUserNum(activeUserNum);
			consumerUserActiveStatistics.setLoginUserNum(loginUserNum);
			consumerUserActiveStatistics.setLoginUserTimes(loginUserTimes);

			List<MerchantActiveStatistics> list = new ArrayList<MerchantActiveStatistics>();
			// 分页 start
			if (start != null && length != null) {// 需要进行分页
				/*
				 * 第一个参数是第几页；第二个参数是每页显示条数。
				 */
				int pageNum = PageHelperUtil.getPageNum(start, length);
				int pageSize = PageHelperUtil.getPageSize(start, length);
				PageHelper.startPage(pageNum, pageSize);
				list = activeStatisticsService.selectPage(consumerUserActiveStatistics);

			} else {
				list = activeStatisticsService.selectPage(consumerUserActiveStatistics);
			}

			int count = activeStatisticsService.count(consumerUserActiveStatistics);
			// 分页 end

			if (list == null || list.isEmpty()) {
				return new ResponseRestEntity<List<MerchantActiveStatistics>>(new ArrayList<MerchantActiveStatistics>(), HttpRestStatus.NOT_FOUND);
			}
			List<MerchantActiveStatistics> listNew = new ArrayList<MerchantActiveStatistics>();
			long surplusActiveUserNum = 0;
			for (MerchantActiveStatistics bean : list) {
				
				if (bean.getTotalUserNum() == null) {
					bean.setTotalUserNum(0l);
				}

				if (bean.getTotalActiveUserNum() == null) {
					bean.setTotalActiveUserNum(0l);
				}

				if (bean.getTotalActiveUserNum() == null && bean.getTotalUserNum() == null) {
					bean.setTotalUserNum(0l);
					bean.setTotalActiveUserNum(0l);
				}
				surplusActiveUserNum = bean.getTotalUserNum()-bean.getTotalActiveUserNum();
				bean.setSurplusActiveUserNum(surplusActiveUserNum);
				listNew.add(bean);
			}
			return new ResponseRestEntity<List<MerchantActiveStatistics>>(listNew, HttpRestStatus.OK, count, count);
		}

		// 查询支付应用
		@PreAuthorize("hasRole('R_BI_SS_Q')")
		@ApiOperation(value = "Query MerchantActiveStatistics", notes = "")
		@RequestMapping(value = "/{statisticsTime}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseRestEntity<MerchantActiveStatistics> selectByPrimaryKey(@PathVariable("statisticsTime") String statisticsTime) {
			MerchantActiveStatistics consumerUserActiveStatistics = activeStatisticsService.selectByPrimaryKey(statisticsTime);
			if (consumerUserActiveStatistics == null) {
				return new ResponseRestEntity<MerchantActiveStatistics>(HttpRestStatus.NOT_FOUND);
			}
			return new ResponseRestEntity<MerchantActiveStatistics>(consumerUserActiveStatistics, HttpRestStatus.OK);
		}

		// 新增支付应用
		@PreAuthorize("hasRole('R_BI_SS_E')")
		@ApiOperation(value = "Add MerchantActiveStatistics", notes = "")
		@RequestMapping(value = "", method = RequestMethod.POST)
		public ResponseRestEntity<Void> create(@Valid @RequestBody MerchantActiveStatistics consumerUserActiveStatistics, BindingResult result, UriComponentsBuilder ucBuilder) {
			
			// 校验
			if (result.hasErrors()) {
				List<ObjectError> list = result.getAllErrors();
				return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
						HttpRestStatusFactory.createStatusMessage(list));
			}
	      // 是否存在相同时间
	    if (activeStatisticsService.isMerchantActiveStatisticsExist(consumerUserActiveStatistics)) {
		     return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
	    }
			activeStatisticsService.insert(consumerUserActiveStatistics);
			// 新增日志
			CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerUserActiveStatistics, CommonLogImpl.BI_REPORT);

			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("/merchantActiveStatistics/{statisticsTime}").buildAndExpand(consumerUserActiveStatistics.getStatisticsTime()).toUri());
			return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
		}

		// 修改支付应用信息
		@PreAuthorize("hasRole('R_BI_SS_E')")
		@ApiOperation(value = "Edit MerchantActiveStatistics", notes = "")
		@RequestMapping(value = "{statisticsTime}", method = RequestMethod.PUT)
		public ResponseRestEntity<MerchantActiveStatistics> update(@PathVariable("statisticsTime") String statisticsTime, @Valid @RequestBody MerchantActiveStatistics consumerUserActiveStatistics, BindingResult result) {

			MerchantActiveStatistics currentPayApp = activeStatisticsService.selectByPrimaryKey(statisticsTime);

			if (currentPayApp == null) {
				return new ResponseRestEntity<MerchantActiveStatistics>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
			}

			currentPayApp.setActiveUserNum(consumerUserActiveStatistics.getActiveUserNum());
			currentPayApp.setLoginUserNum(consumerUserActiveStatistics.getLoginUserNum());
			currentPayApp.setLoginUserTimes(consumerUserActiveStatistics.getLoginUserTimes());
			currentPayApp.setSurplusActiveUserNum(consumerUserActiveStatistics.getSurplusActiveUserNum());
			currentPayApp.setTotalActiveUserNum(consumerUserActiveStatistics.getTotalActiveUserNum());
			currentPayApp.setTotalUserNum(consumerUserActiveStatistics.getTotalUserNum());
			if (result.hasErrors()) {
				List<ObjectError> list = result.getAllErrors();

				return new ResponseRestEntity<MerchantActiveStatistics>(currentPayApp, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
			}

			activeStatisticsService.updateByPrimaryKey(currentPayApp);
			// 修改日志
			CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentPayApp, CommonLogImpl.BI_REPORT);
			return new ResponseRestEntity<MerchantActiveStatistics>(currentPayApp, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
		}

		// 修改部分支付应用信息
		@PreAuthorize("hasRole('R_BI_SS_E')")
		@ApiOperation(value = "Edit Part MerchantActiveStatistics", notes = "")
		@RequestMapping(value = "{statisticsTime}", method = RequestMethod.PATCH)
		public ResponseRestEntity<MerchantActiveStatistics> updateSelective(@PathVariable("statisticsTime") String statisticsTime, @RequestBody MerchantActiveStatistics consumerUserActiveStatistics) {

			MerchantActiveStatistics currentPayApp = activeStatisticsService.selectByPrimaryKey(statisticsTime);

			if (currentPayApp == null) {
				return new ResponseRestEntity<MerchantActiveStatistics>(HttpRestStatus.NOT_FOUND);
			}
			activeStatisticsService.updateByPrimaryKeySelective(consumerUserActiveStatistics);
			//修改日志
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, consumerUserActiveStatistics,CommonLogImpl.BI_REPORT);
			return new ResponseRestEntity<MerchantActiveStatistics>(consumerUserActiveStatistics, HttpRestStatus.OK);
		}

		// 删除指定支付应用
		@PreAuthorize("hasRole('R_BI_SS_E')")
		@ApiOperation(value = "Delete MerchantActiveStatistics", notes = "")
		@RequestMapping(value = "/{statisticsTime}", method = RequestMethod.DELETE)
		public ResponseRestEntity<MerchantActiveStatistics> delete(@PathVariable("statisticsTime") String statisticsTime) {

			MerchantActiveStatistics consumerUserActiveStatistics = activeStatisticsService.selectByPrimaryKey(statisticsTime);
			if (consumerUserActiveStatistics == null) {
				return new ResponseRestEntity<MerchantActiveStatistics>(HttpRestStatus.NOT_FOUND);
			}

			activeStatisticsService.deleteByPrimaryKey(statisticsTime);
			// 删除日志开始
			MerchantActiveStatistics delBean = new MerchantActiveStatistics();
			delBean.setStatisticsTime(statisticsTime);

			CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean, CommonLogImpl.BI_REPORT);
			// 删除日志结束
			return new ResponseRestEntity<MerchantActiveStatistics>(HttpRestStatus.NO_CONTENT);
		}
}
