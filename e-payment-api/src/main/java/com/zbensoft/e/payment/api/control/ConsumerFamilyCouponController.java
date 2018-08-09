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
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyCouponService;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.CouponService;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.Coupon;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumerFamilyCoupon")
@RestController
public class ConsumerFamilyCouponController {
	@Autowired
	ConsumerFamilyCouponService consumerFamilyCouponService;
	
	@Autowired
	CouponService couponService;
	
	@Autowired
	ConsumerFamilyService consumerFamilyService;
	
	@Autowired
	ConsumerUserClapService consumerUserClapService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_COUPON_B_F_Q')")
	@ApiOperation(value = "Query ConsumerFamilyCoupon，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerFamilyCoupon>> selectPage(
			@RequestParam(required = false) String familyId,
			@RequestParam(required = false) String couponId,
			@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String tradeSeq,
			@RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		ConsumerFamilyCoupon consumerFamilyCoupon = new ConsumerFamilyCoupon();
		// 必须输入一个进行查询
		if ((couponId == null || "".equals(couponId)) && (familyId == null || "".equals(familyId)) && (status == null || "".equals(status))
				&& (idNumber == null || "".equals(idNumber))) {
			return new ResponseRestEntity<List<ConsumerFamilyCoupon>>(new ArrayList<ConsumerFamilyCoupon>(), HttpRestStatus.NOT_FOUND);
		}
		
		if (idNumber == null || "".equals(idNumber)) {
			consumerFamilyCoupon.setFamilyId(familyId);
		} else {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(idNumber);
			if (consumerUserClap == null) {
				return new ResponseRestEntity<List<ConsumerFamilyCoupon>>(new ArrayList<ConsumerFamilyCoupon>(), HttpRestStatus.NOT_FOUND);
			} else {
				if (familyId == null || "".equals(familyId)) {
					consumerFamilyCoupon.setFamilyId(consumerUserClap.getFamilyId());
				} else {
					if (familyId.equals(consumerUserClap.getFamilyId())) {
						consumerFamilyCoupon.setFamilyId(familyId);
					} else {
						return new ResponseRestEntity<List<ConsumerFamilyCoupon>>(new ArrayList<ConsumerFamilyCoupon>(), HttpRestStatus.NOT_FOUND);
					}
				}
			}

		}
		
		
		consumerFamilyCoupon.setCouponId(couponId);
	
		   consumerFamilyCoupon.setStatus(status);
	
		consumerFamilyCoupon.setTradeSeq(tradeSeq);
		int count = consumerFamilyCouponService.count(consumerFamilyCoupon);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerFamilyCoupon>>(new ArrayList<ConsumerFamilyCoupon>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerFamilyCoupon> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerFamilyCouponService.selectPage(consumerFamilyCoupon);

		} else {
			list = consumerFamilyCouponService.selectPage(consumerFamilyCoupon);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerFamilyCoupon>>(new ArrayList<ConsumerFamilyCoupon>(),HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerFamilyCoupon> listNew = new ArrayList<ConsumerFamilyCoupon>();
		for(ConsumerFamilyCoupon bean:list){
			Coupon coupon = couponService.selectByPrimaryKey(bean.getCouponId());
			ConsumerFamily  consumerFamily = consumerFamilyService.selectByPrimaryKey(bean.getFamilyId());
			if(coupon!=null){
				bean.setCouponName(coupon.getName());
				bean.setUserStartTime(DateUtil.convertDateToFormatString(coupon.getUserStartTime()));
				bean.setUserEndTime(DateUtil.convertDateToFormatString(coupon.getUserEndTime()));
			}
			if(consumerFamily!=null){
				bean.setFamilyName(consumerFamily.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ConsumerFamilyCoupon>>(listNew, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_COUPON_B_F_Q')")
	@ApiOperation(value = "Query ConsumerFamilyCoupon", notes = "")
	@RequestMapping(value = "/{couponId}/{familyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerFamilyCoupon> selectByPrimaryKey(@PathVariable("familyId") String familyId,
			@PathVariable("couponId") String couponId) {
		ConsumerFamilyCoupon bean = new ConsumerFamilyCoupon();
		bean.setFamilyId(familyId);
		bean.setCouponId(couponId);
		ConsumerFamilyCoupon consumerFamilyCoupon = consumerFamilyCouponService.selectByPrimaryKey(bean);
		if (consumerFamilyCoupon == null) {
			return new ResponseRestEntity<ConsumerFamilyCoupon>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumerFamilyCoupon>(consumerFamilyCoupon, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_B_F_E')")
	@ApiOperation(value = "Add ConsumerFamilyCoupon", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createConsumerFamilyCoupon(@Valid @RequestBody ConsumerFamilyCoupon consumerFamilyCoupon,BindingResult result, UriComponentsBuilder ucBuilder) {
		consumerFamilyCoupon.setIdNumber(CommonFun.getRelVid(consumerFamilyCoupon.getIdNumber()));
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		
		if("familyId".equals(consumerFamilyCoupon.getFamilyId())){
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(consumerFamilyCoupon.getIdNumber());
			if(consumerUserClap==null){
				return new ResponseRestEntity<Void>(HttpRestStatus.CONSUMER_NOT_FOUND);
			}else{
				consumerFamilyCoupon.setFamilyId(consumerUserClap.getFamilyId());
			}
		}
		
		ConsumerFamilyCoupon bean = consumerFamilyCouponService.selectByPrimaryKey(consumerFamilyCoupon);
		if(bean !=null){
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		consumerFamilyCouponService.insert(consumerFamilyCoupon);
		//新增日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerFamilyCoupon,CommonLogImpl.COUPON_MANAGE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumerFamilyCoupon/{id}").buildAndExpand(consumerFamilyCoupon.getCouponId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_COUPON_B_F_E')")
	@ApiOperation(value = "Edit ConsumerFamilyCoupon", notes = "")
	@RequestMapping(value = "/{couponId}/{familyId}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerFamilyCoupon> updateConsumerFamilyCoupon(@PathVariable("couponId") String couponId,
			@PathVariable("familyId") String familyId,@Valid @RequestBody ConsumerFamilyCoupon consumerFamilyCoupon, BindingResult result) {
		ConsumerFamilyCoupon bean = new ConsumerFamilyCoupon();
		bean.setFamilyId(familyId);
		bean.setCouponId(couponId);
		ConsumerFamilyCoupon currentConsumerFamilyCoupon = consumerFamilyCouponService.selectByPrimaryKey(bean);

		if (currentConsumerFamilyCoupon == null) {
			return new ResponseRestEntity<ConsumerFamilyCoupon>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentConsumerFamilyCoupon.setFamilyId(consumerFamilyCoupon.getFamilyId());
		currentConsumerFamilyCoupon.setCouponId(consumerFamilyCoupon.getCouponId());
		currentConsumerFamilyCoupon.setStatus(consumerFamilyCoupon.getStatus());
		currentConsumerFamilyCoupon.setTradeSeq(consumerFamilyCoupon.getTradeSeq());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<ConsumerFamilyCoupon>(currentConsumerFamilyCoupon,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		consumerFamilyCouponService.updateByPrimaryKey(currentConsumerFamilyCoupon);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerFamilyCoupon,CommonLogImpl.COUPON_MANAGE);
		return new ResponseRestEntity<ConsumerFamilyCoupon>(currentConsumerFamilyCoupon, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_COUPON_B_F_E')")
	@ApiOperation(value = "Edit Part ConsumerFamilyCoupon", notes = "")
	@RequestMapping(value = "/{couponId}/{familyId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerFamilyCoupon> updateConsumerFamilyCouponSelective(@PathVariable("couponId") String couponId,
			@PathVariable("familyId") String familyId,
			@RequestBody ConsumerFamilyCoupon consumerFamilyCoupon) {

		ConsumerFamilyCoupon bean = new ConsumerFamilyCoupon();
		bean.setFamilyId(familyId);
		bean.setCouponId(couponId);
		ConsumerFamilyCoupon currentConsumerFamilyCoupon = consumerFamilyCouponService.selectByPrimaryKey(bean);

		if (currentConsumerFamilyCoupon == null) {
			return new ResponseRestEntity<ConsumerFamilyCoupon>(HttpRestStatus.NOT_FOUND);
		}
		currentConsumerFamilyCoupon.setFamilyId(consumerFamilyCoupon.getFamilyId());
		currentConsumerFamilyCoupon.setCouponId(consumerFamilyCoupon.getCouponId());
		currentConsumerFamilyCoupon.setStatus(consumerFamilyCoupon.getStatus());
		currentConsumerFamilyCoupon.setTradeSeq(consumerFamilyCoupon.getTradeSeq());
		consumerFamilyCouponService.updateByPrimaryKeySelective(currentConsumerFamilyCoupon);

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerFamilyCoupon,CommonLogImpl.COUPON_MANAGE);
		return new ResponseRestEntity<ConsumerFamilyCoupon>(currentConsumerFamilyCoupon, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_B_F_E')")
	@ApiOperation(value = "Delete ConsumerFamilyCoupon", notes = "")
	@RequestMapping(value = "/{couponId}/{familyId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerFamilyCoupon> deleteConsumerFamilyCoupon(@PathVariable("couponId") String couponId,
			@PathVariable("familyId") String familyId) {
		ConsumerFamilyCoupon bean = new ConsumerFamilyCoupon();
		bean.setFamilyId(familyId);
		bean.setCouponId(couponId);
		ConsumerFamilyCoupon consumerFamilyCoupon = consumerFamilyCouponService.selectByPrimaryKey(bean);
		if (consumerFamilyCoupon == null) {
			return new ResponseRestEntity<ConsumerFamilyCoupon>(HttpRestStatus.NOT_FOUND);
		}

		consumerFamilyCouponService.deleteByPrimaryKey(bean);
		//删除日志开始
		ConsumerFamilyCoupon coupo = new ConsumerFamilyCoupon();
				coupo.setFamilyId(familyId);
		coupo.setCouponId(couponId);
						    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, coupo,CommonLogImpl.COUPON_MANAGE);
							//删除日志结束
		return new ResponseRestEntity<ConsumerFamilyCoupon>(HttpRestStatus.NO_CONTENT);
	}

}