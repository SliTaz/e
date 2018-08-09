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
import com.zbensoft.e.payment.api.service.api.BalanceStatisticsService;
import com.zbensoft.e.payment.db.domain.BalanceStatistics;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/balanceStatistics")
@RestController
public class BalanceStatisticsController {
	@Autowired
	BalanceStatisticsService balanceStatisticsService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_BI_BS_Q')")
	@ApiOperation(value = "Query BalanceStatistics，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<BalanceStatistics>> selectPage(@RequestParam(required = false) String statisticsTimeStart,
			@RequestParam(required = false) String statisticsTimeEnd,
			@RequestParam(required = false) String start,@RequestParam(required = false) String length) {
		BalanceStatistics balanceStatistics = new BalanceStatistics();
		balanceStatistics.setStatisticsTimeStart(statisticsTimeStart);
		balanceStatistics.setStatisticsTimeEnd(statisticsTimeEnd);
		int count = balanceStatisticsService.count(balanceStatistics);
		if (count == 0) {
			return new ResponseRestEntity<List<BalanceStatistics>>(new ArrayList<BalanceStatistics>(), HttpRestStatus.NOT_FOUND);
		}
		List<BalanceStatistics> list = null;
		// 分页 start
				if (start != null && length != null) {// 需要进行分页
					/*
					 * 第一个参数是第几页；第二个参数是每页显示条数。
					 */
					int pageNum = PageHelperUtil.getPageNum(start, length);
					int pageSize = PageHelperUtil.getPageSize(start, length);
					PageHelper.startPage(pageNum, pageSize);
					list = balanceStatisticsService.selectPage(balanceStatistics);
					// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
					// System.out.println("list.size:"+list.size());

				} else {
					list = balanceStatisticsService.selectPage(balanceStatistics);
				}

				// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<BalanceStatistics>>(new ArrayList<BalanceStatistics>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<BalanceStatistics>>(list, HttpRestStatus.OK,count,count);
	}

	@PreAuthorize("hasRole('R_BI_BS_Q')")
	@ApiOperation(value = "Query BalanceStatistics", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<BalanceStatistics> selectByPrimaryKey(@PathVariable("id") String id) {
		BalanceStatistics balanceStatistics = balanceStatisticsService.selectByPrimaryKey(id);
		if (balanceStatistics == null) {
			return new ResponseRestEntity<BalanceStatistics>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<BalanceStatistics>(balanceStatistics, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BI_BS_E')")
	@ApiOperation(value = "Add BalanceStatistics", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createBalanceStatistics(@Valid @RequestBody BalanceStatistics balanceStatistics, BindingResult result, UriComponentsBuilder ucBuilder) {
		//balanceStatistics.setBankId(System.currentTimeMillis()+"");
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		
		balanceStatisticsService.insert(balanceStatistics);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, balanceStatistics,CommonLogImpl.PAYMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/balanceStatistics/{id}").buildAndExpand(balanceStatistics.getStatisticsTime()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("balanceStatistics.create.created.message"));
	}

	@PreAuthorize("hasRole('R_BI_BS_E')")
	@ApiOperation(value = "Edit BalanceStatistics", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<BalanceStatistics> updateBalanceStatistics(@PathVariable("id") String id, @Valid @RequestBody BalanceStatistics balanceStatistics, BindingResult result) {

		BalanceStatistics currentBalanceStatistics = balanceStatisticsService.selectByPrimaryKey(id);

		if (currentBalanceStatistics == null) {
			return new ResponseRestEntity<BalanceStatistics>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentBalanceStatistics.setStatisticsTime(balanceStatistics.getStatisticsTime());
		currentBalanceStatistics.setBuyerBalance(balanceStatistics.getBuyerBalance());
		currentBalanceStatistics.setSellerBalance(balanceStatistics.getSellerBalance());
		currentBalanceStatistics.setRemark(balanceStatistics.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<BalanceStatistics>(currentBalanceStatistics,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		balanceStatisticsService.updateByPrimaryKey(currentBalanceStatistics);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBalanceStatistics,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<BalanceStatistics>(currentBalanceStatistics, HttpRestStatus.OK,localeMessageSourceService.getMessage("balanceStatistics.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_BI_BS_E')")
	@ApiOperation(value = "Edit Part BalanceStatistics", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<BalanceStatistics> updateBalanceStatisticsSelective(@PathVariable("id") String id, @RequestBody BalanceStatistics balanceStatistics) {

		BalanceStatistics currentBalanceStatistics = balanceStatisticsService.selectByPrimaryKey(id);

		if (currentBalanceStatistics == null) {
			return new ResponseRestEntity<BalanceStatistics>(HttpRestStatus.NOT_FOUND);
		}
		currentBalanceStatistics.setStatisticsTime(balanceStatistics.getStatisticsTime());
		currentBalanceStatistics.setBuyerBalance(balanceStatistics.getBuyerBalance());
		currentBalanceStatistics.setSellerBalance(balanceStatistics.getSellerBalance());
		currentBalanceStatistics.setRemark(balanceStatistics.getRemark());
		balanceStatisticsService.updateByPrimaryKeySelective(balanceStatistics);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, balanceStatistics,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<BalanceStatistics>(currentBalanceStatistics, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BI_BS_E')")
	@ApiOperation(value = "Delete BalanceStatistics", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<BalanceStatistics> deleteBalanceStatistics(@PathVariable("id") String id) {

		balanceStatisticsService.deleteByPrimaryKey(id);
		
		//删除日志开始
		BalanceStatistics delBean = new BalanceStatistics();
		delBean.setStatisticsTime(id);;
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.PAYMENT);
		//删除日志结束
		return new ResponseRestEntity<BalanceStatistics>(HttpRestStatus.NO_CONTENT);
	}

}