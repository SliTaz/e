package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
import com.zbensoft.e.payment.api.service.api.ProfitStatementService;
import com.zbensoft.e.payment.db.domain.ProfitStatement;

import io.swagger.annotations.ApiOperation;
@RequestMapping(value = "/profitStatement")
@RestController
public class ProfitStatementController {
	@Autowired
	ProfitStatementService profitStatementService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	

	@PreAuthorize("hasRole('R_BI_PS_Q')")
	@ApiOperation(value = "Query profitStatement,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ProfitStatement>> selectPage(@RequestParam(required = false) String statisticsTimeStart,
			@RequestParam(required = false) String statisticsTimeEnd,
			@RequestParam(required = false) Double amount,
			@RequestParam(required = false) String remark,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ProfitStatement profitStatement = new ProfitStatement();
		profitStatement.setStatisticsTimeStart(statisticsTimeStart);
		profitStatement.setStatisticsTimeEnd(statisticsTimeEnd);
		profitStatement.setAmount(amount);
		profitStatement.setRemark(remark);

		int count = profitStatementService.count(profitStatement);
		if (count == 0) {
			return new ResponseRestEntity<List<ProfitStatement>>(new ArrayList<ProfitStatement>(), HttpRestStatus.NOT_FOUND);
		}

		List<ProfitStatement> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = profitStatementService.selectPage(profitStatement);
		} else {
			list = profitStatementService.selectPage(profitStatement);
		}

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ProfitStatement>>(new ArrayList<ProfitStatement>(), HttpRestStatus.NOT_FOUND);
		}
	
		return new ResponseRestEntity<List<ProfitStatement>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_BI_PS_Q')")
	@ApiOperation(value = "Query ProfitStatement", notes = "")
	@RequestMapping(value = "/{statisticsTime}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ProfitStatement> selectByPrimaryKey(@PathVariable("statisticsTime") String statisticsTime) {
		ProfitStatement profitStatement = profitStatementService.selectByPrimaryKey(statisticsTime);
		if (profitStatement == null) {
			return new ResponseRestEntity<ProfitStatement>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ProfitStatement>(profitStatement, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BI_PS_E')")
	@ApiOperation(value = "Add ProfitStatement", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtaskInstance(@RequestBody ProfitStatement profitStatement,BindingResult result,  UriComponentsBuilder ucBuilder) {
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		 }
		profitStatementService.insert(profitStatement);
		//新增日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, profitStatement,CommonLogImpl.ACCOUNTING);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/profitStatement/{statisticsTime}").buildAndExpand(profitStatement.getStatisticsTime()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_BI_PS_E')")
	@ApiOperation(value = "Edit profitStatement", notes = "")
	@RequestMapping(value = "{statisticsTime}", method = RequestMethod.PUT)
	public ResponseRestEntity<ProfitStatement> updatetask(@PathVariable("statisticsTime") String statisticsTime, @RequestBody ProfitStatement profitStatement) {

		ProfitStatement type = profitStatementService.selectByPrimaryKey(statisticsTime);

		if (type == null) {
			return new ResponseRestEntity<ProfitStatement>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setStatisticsTime(profitStatement.getStatisticsTime());
		type.setAmount(profitStatement.getAmount());
		type.setRemark(profitStatement.getRemark());
		profitStatementService.updateByPrimaryKey(type);
		//修改日志
       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.ACCOUNTING);
		return new ResponseRestEntity<ProfitStatement>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_BI_PS_E')")
	@ApiOperation(value = "Edit Part TaskInstance", notes = "")
	@RequestMapping(value = "{statisticsTime}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ProfitStatement> updatetaskSelective(@PathVariable("statisticsTime") String statisticsTime, @RequestBody ProfitStatement profitStatement) {

		ProfitStatement type = profitStatementService.selectByPrimaryKey(statisticsTime);

		if (type == null) {
			return new ResponseRestEntity<ProfitStatement>(HttpRestStatus.NOT_FOUND);
		}
		profitStatementService.updateByPrimaryKeySelective(profitStatement);
		//修改日志
	    CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, profitStatement,CommonLogImpl.ACCOUNTING);
		return new ResponseRestEntity<ProfitStatement>(type, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BI_PS_E')")
	@ApiOperation(value = "Delete TaskInstance", notes = "")
	@RequestMapping(value = "/{statisticsTime}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ProfitStatement> deletetaskInstance(@PathVariable("statisticsTime") String statisticsTime) {

		ProfitStatement task = profitStatementService.selectByPrimaryKey(statisticsTime);
		if (task == null) {
			return new ResponseRestEntity<ProfitStatement>(HttpRestStatus.NOT_FOUND);
		}

		profitStatementService.deleteByPrimaryKey(statisticsTime);
		//删除日志开始
		ProfitStatement delBean = new ProfitStatement();
		delBean.setStatisticsTime(statisticsTime);           

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.ACCOUNTING);
		//删除日志结束
		return new ResponseRestEntity<ProfitStatement>(HttpRestStatus.NO_CONTENT);
	}
}
