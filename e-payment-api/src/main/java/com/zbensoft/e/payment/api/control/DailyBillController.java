package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import com.zbensoft.e.payment.api.service.api.DailyBillService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.DailyBill;
import com.zbensoft.e.payment.db.domain.MerchantUser;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/dailyBill")
@RestController
public class DailyBillController {
	@Autowired
	DailyBillService dailyBillService;
	@Autowired
	MerchantUserService merchantUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询日账单，支持分页
	@ApiOperation(value = "Query DailyBill，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<DailyBill>> selectPage(@RequestParam(required = false) String id,
			 @RequestParam(required = false) String billDateStart,
			 @RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String billDateEnd, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		DailyBill dailyBill = new DailyBill();
		
		// 输入idNumber查询
		if ( (id == null || "".equals(id)) && (idNumber == null || "".equals(idNumber))) {
			return new ResponseRestEntity<List<DailyBill>>(new ArrayList<DailyBill>(),
					HttpRestStatus.NOT_FOUND);
		}
		
		if (idNumber == null || "".equals(idNumber)) {
			dailyBill.setUserId(id);
		} else {
			MerchantUser merchantUser = merchantUserService.selectByIdNumber(idNumber);
			if (merchantUser == null) {
				return new ResponseRestEntity<List<DailyBill>>(new ArrayList<DailyBill>(), HttpRestStatus.NOT_FOUND);
			} else {
				if (id == null || "".equals(id)) {
					dailyBill.setUserId(merchantUser.getUserId());
				} else {
					if (id.equals(merchantUser.getUserId())) {
						dailyBill.setUserId(id);
					} else {
						return new ResponseRestEntity<List<DailyBill>>(new ArrayList<DailyBill>(), HttpRestStatus.NOT_FOUND);
					}
				}
			}

		}

		dailyBill.setBillDateStartSer(billDateStart);
		dailyBill.setBillDateEndSer(billDateEnd);

		List<DailyBill> list = new ArrayList<DailyBill>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = dailyBillService.selectPage(dailyBill);

		} else {
			list = dailyBillService.selectPage(dailyBill);
		}

		int count = dailyBillService.count(dailyBill);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<DailyBill>>(new ArrayList<DailyBill>(), HttpRestStatus.NOT_FOUND);
		}
		List<DailyBill> listNew = new ArrayList<DailyBill>();
		for(DailyBill bean:list){
			MerchantUser merchantUsers = merchantUserService.selectByPrimaryKey(bean.getUserId());
			if(merchantUsers!=null){
				bean.setIdNumber(merchantUsers.getIdNumber());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<DailyBill>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询日账单
	@ApiOperation(value = "Query DailyBill", notes = "")
	@RequestMapping(value = "/{userId}/{billDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<DailyBill> selectByPrimaryKey(@PathVariable("userId") String userId,@PathVariable("billDate") String billDate) {
		DailyBill bean = new DailyBill();
		bean.setUserId(userId);
		bean.setBillDate(billDate);
		
		DailyBill dailyBill = dailyBillService.selectByPrimaryKey(bean);
		if (dailyBill == null) {
			return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<DailyBill>(dailyBill, HttpRestStatus.OK);
	}

	// 新增日账单
	@ApiOperation(value = "Add DailyBill", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody DailyBill dailyBill, BindingResult result,
			UriComponentsBuilder ucBuilder) {

		if (dailyBillService.isExist(dailyBill)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,
					localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		dailyBill.setCreateTime(PageHelperUtil.getCurrentDate());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		dailyBillService.insert(dailyBill);
		//新增日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, dailyBill,CommonLogImpl.FINANCE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(
				ucBuilder.path("/dailyBill/{userId}").buildAndExpand(dailyBill.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,
				localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改日账单信息
	@ApiOperation(value = "Edit DailyBill", notes = "")
	@RequestMapping(value = "/{userId}/{billDate}", method = RequestMethod.PUT)
	public ResponseRestEntity<DailyBill> update(@PathVariable("userId") String userId,@PathVariable("billDate") String billDate,
			@Valid @RequestBody DailyBill dailyBill, BindingResult result) {

		DailyBill bean = new DailyBill();
		bean.setUserId(userId);
		bean.setBillDate(billDate);
		
		DailyBill currentDailyBill = dailyBillService.selectByPrimaryKey(bean);

		if (currentDailyBill == null) {
			return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOT_FOUND,
					localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentDailyBill.setBillDate(dailyBill.getBillDate());
		currentDailyBill.setBorrow(dailyBill.getBorrow());
		currentDailyBill.setLoan(dailyBill.getLoan());
		currentDailyBill.setStartMoney(dailyBill.getStartMoney());
		currentDailyBill.setEndMoney(dailyBill.getEndMoney());
		currentDailyBill.setRemark(dailyBill.getRemark());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<DailyBill>(currentDailyBill, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		dailyBillService.updateByPrimaryKey(currentDailyBill);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentDailyBill,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<DailyBill>(currentDailyBill, HttpRestStatus.OK,
				localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分日账单信息
	@ApiOperation(value = "Edit Part DailyBill", notes = "")
	@RequestMapping(value = "/{userId}/{billDate}", method = RequestMethod.PATCH)
	public ResponseRestEntity<DailyBill> updateSelective(@PathVariable("userId") String userId,@PathVariable("billDate") String billDate,
			@RequestBody DailyBill dailyBill) {

		DailyBill bean = new DailyBill();
		bean.setUserId(userId);
		bean.setBillDate(billDate);
		
		DailyBill currentDailyBill = dailyBillService.selectByPrimaryKey(bean);

		if (currentDailyBill == null) {
			return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOT_FOUND);
		}
		dailyBill.setUserId(userId);
		dailyBillService.updateByPrimaryKeySelective(dailyBill);// ?
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, dailyBill,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<DailyBill>(currentDailyBill, HttpRestStatus.OK);
	}

	// 删除指定日账单
	@ApiOperation(value = "Delete DailyBill", notes = "")
	@RequestMapping(value = "/{userId}/{billDate}", method = RequestMethod.DELETE)
	public ResponseRestEntity<DailyBill> delete(@PathVariable("userId") String userId,@PathVariable("billDate") String billDate) {

		DailyBill bean = new DailyBill();
		bean.setUserId(userId);
		bean.setBillDate(billDate);
		
		DailyBill dailyBill = dailyBillService.selectByPrimaryKey(bean);
		if (dailyBill == null) {
			return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOT_FOUND);
		}

		dailyBillService.deleteByPrimaryKey(bean);
		//删除日志开始
		DailyBill delBean = new DailyBill();
		delBean.setUserId(userId);
		delBean.setBillDate(billDate);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FINANCE);
		//删除日志结束
		return new ResponseRestEntity<DailyBill>(HttpRestStatus.NO_CONTENT);
	}

	// 查询月账单
	@ApiOperation(value = "Query DailyBill，Support paging", notes = "")
	@RequestMapping(value = "/getMonthBill", method = RequestMethod.GET)
	public ResponseRestEntity<DailyBill> getDalyBill(@RequestParam(required = false) String user_id,
			@RequestParam(required = false) String user_name) {
		if (user_id == null || user_id.isEmpty()) {
			if (user_name == null || user_name.isEmpty()) {
				return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOTEMPTY, "用户编号和用户名称不能都为空");
			} else {
				MerchantUser merchantUser = merchantUserService.selectByUserName(user_name);
				if (merchantUser == null) {
					return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOT_EXIST, "用户不存在");
				}
				user_id = merchantUser.getUserId();
			}
		} else {
			MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(user_id);
			if (merchantUser == null) {
				return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOT_EXIST, "用户不存在");
			}
		}
		DailyBill dailyBill = new DailyBill();
		dailyBill.setUserId(user_id);
		dailyBill.setBillDateStartSer(
				DateUtil.convertDateToString(new Date(System.currentTimeMillis()), "YYYY-MM-01") + " 00:00:00");

		List<DailyBill> list = dailyBillService.selectPage(dailyBill);

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOT_FOUND);
		}
		DailyBill retDailyBill = new DailyBill();
		Double borrow = 0d;
		Double loan = 0d;
		for (DailyBill dailyBillTmp : list) {
			borrow = DoubleUtil.add(borrow, dailyBillTmp.getLoan());
			loan = DoubleUtil.add(loan, dailyBillTmp.getLoan());
		}
		retDailyBill.setBorrow(borrow);
		retDailyBill.setLoan(loan);
		return new ResponseRestEntity<DailyBill>(retDailyBill, HttpRestStatus.OK);
	}

}