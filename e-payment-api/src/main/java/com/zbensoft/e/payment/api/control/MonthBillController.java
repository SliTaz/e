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
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.MonthBillService;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.MonthBill;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/monthBill")
@RestController
public class MonthBillController {
	@Autowired
	MonthBillService monthBillService;

	@Autowired
	ConsumerUserClapService consumerUserClapService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	//查询日账单，支持分页
	@PreAuthorize("hasRole('R_TRADE_M_B_Q')")
	@ApiOperation(value = "Query MonthBill，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MonthBill>> selectPage(
			@RequestParam(required = false) String id,
			@RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String billDateStart,
			@RequestParam(required = false) String billDateEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		MonthBill monthBill = new MonthBill();
		// 输入idNumber查询
		if ( (id == null || "".equals(id)) && (idNumber == null || "".equals(idNumber))) {
			return new ResponseRestEntity<List<MonthBill>>(new ArrayList<MonthBill>(),
					HttpRestStatus.NOT_FOUND);
		}
		
		if (idNumber == null || "".equals(idNumber)) {
			monthBill.setUserId(id);
		} else {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(idNumber);
			if (consumerUserClap == null) {
				return new ResponseRestEntity<List<MonthBill>>(new ArrayList<MonthBill>(), HttpRestStatus.NOT_FOUND);
			} else {
				if (id == null || "".equals(id)) {
					monthBill.setUserId(consumerUserClap.getUserId());
				} else {
					if (id.equals(consumerUserClap.getUserId())) {
						monthBill.setUserId(id);
					} else {
						return new ResponseRestEntity<List<MonthBill>>(new ArrayList<MonthBill>(), HttpRestStatus.NOT_FOUND);
					}
				}
			}

		}
		monthBill.setBillDateStartSer(billDateStart);
		monthBill.setBillDateEndSer(billDateEnd);
		
		List<MonthBill> list = new ArrayList<MonthBill>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = monthBillService.selectPage(monthBill);

		} else {
			list = monthBillService.selectPage(monthBill);
		}

		int count = monthBillService.count(monthBill);
		// 分页 end
		
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MonthBill>>(new ArrayList<MonthBill>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<MonthBill>>(list, HttpRestStatus.OK,count,count);
	}

	//查询日账单
	@PreAuthorize("hasRole('R_TRADE_M_B_Q')")
	@ApiOperation(value = "Query MonthBill", notes = "")
	@RequestMapping(value = "/{userId}/{billDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MonthBill> selectByPrimaryKey(@PathVariable("userId") String userId,@PathVariable("billDate") String billDate) {
		MonthBill bean = new MonthBill();
		bean.setUserId(userId);
		bean.setBillDate(billDate);
		MonthBill monthBill = monthBillService.selectByPrimaryKey(bean);
		if (monthBill == null) {
			return new ResponseRestEntity<MonthBill>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MonthBill>(monthBill, HttpRestStatus.OK);
	}

	//新增日账单
	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Add MonthBill", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody MonthBill monthBill, BindingResult result, UriComponentsBuilder ucBuilder) {

		
		if (monthBillService.isExist(monthBill)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		monthBill.setCreateTime(PageHelperUtil.getCurrentDate());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		
		monthBillService.insert(monthBill);
		//新增日志
       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, monthBill,CommonLogImpl.FINANCE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/monthBill/{userId}").buildAndExpand(monthBill.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	//修改日账单信息
	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Edit MonthBill", notes = "")
	@RequestMapping(value = "/{userId}/{billDate}", method = RequestMethod.PUT)
	public ResponseRestEntity<MonthBill> update(@PathVariable("userId") String userId,@PathVariable("billDate") String billDate, @Valid @RequestBody MonthBill monthBill, BindingResult result) {

		MonthBill bean = new MonthBill();
		bean.setUserId(userId);
		bean.setBillDate(billDate);
		MonthBill currentMonthBill = monthBillService.selectByPrimaryKey(bean);

		if (currentMonthBill == null) {
			return new ResponseRestEntity<MonthBill>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentMonthBill.setBorrow(monthBill.getBorrow());
		currentMonthBill.setLoan(monthBill.getLoan());
		currentMonthBill.setStartMoney(monthBill.getStartMoney());
		currentMonthBill.setEndMoney(monthBill.getEndMoney());
		currentMonthBill.setRemark(monthBill.getRemark());
	
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<MonthBill>(currentMonthBill,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		
		monthBillService.updateByPrimaryKey(currentMonthBill);
		//修改日志
       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMonthBill,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<MonthBill>(currentMonthBill, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	//修改部分日账单信息
	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Edit Part MonthBill", notes = "")
	@RequestMapping(value = "/{userId}/{billDate}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MonthBill> updateSelective(@PathVariable("userId") String userId,@PathVariable("billDate") String billDate, @RequestBody MonthBill monthBill) {

		MonthBill bean = new MonthBill();
		bean.setUserId(userId);
		bean.setBillDate(billDate);
		MonthBill currentMonthBill = monthBillService.selectByPrimaryKey(bean);

		if (currentMonthBill == null) {
			return new ResponseRestEntity<MonthBill>(HttpRestStatus.NOT_FOUND);
		}
		monthBillService.updateByPrimaryKeySelective(monthBill);//?
		//修改日志
	       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, monthBill,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<MonthBill>(currentMonthBill, HttpRestStatus.OK);
	}

	//删除指定日账单
	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Delete MonthBill", notes = "")
	@RequestMapping(value = "/{userId}/{billDate}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MonthBill> delete(@PathVariable("userId") String userId,@PathVariable("billDate") String billDate) {

		MonthBill bean = new MonthBill();
		bean.setUserId(userId);
		bean.setBillDate(billDate);
		MonthBill monthBill = monthBillService.selectByPrimaryKey(bean);
		if (monthBill == null) {
			return new ResponseRestEntity<MonthBill>(HttpRestStatus.NOT_FOUND);
		}

		monthBillService.deleteByPrimaryKey(bean);
		//删除日志开始
		MonthBill delBean = new MonthBill();
		delBean.setUserId(userId);
		delBean.setBillDate(billDate);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FINANCE);
		//删除日志结束
		return new ResponseRestEntity<MonthBill>(HttpRestStatus.NO_CONTENT);
	}

	// 查询近5个月的账单情况
	@PreAuthorize("hasRole('R_TRADE_M_B_Q') or hasRole('CONSUMER')")
	@ApiOperation(value = "Query Last Five MonthBill", notes = "")
	@RequestMapping(value = "/getLastFiveMonthBill", method = RequestMethod.GET)
	public ResponseRestEntity<List<MonthBill>> queryLastFiveMonth(@RequestParam(required = true) String userId, @RequestParam(required = true) String monthArrString) {

		List<MonthBill> monthBillList=new ArrayList<MonthBill>();
		if(null == monthArrString || "".equals(monthArrString))
		{
			return new ResponseRestEntity<List<MonthBill>>(HttpRestStatus.NO_CONTENT);
		}
		
		String monthArr[] = monthArrString.split(",");
		monthBillList=monthBillService.queryLastFiveMonth(userId,monthArr);
		return new ResponseRestEntity<List<MonthBill>>(monthBillList,HttpRestStatus.OK);
	}

}