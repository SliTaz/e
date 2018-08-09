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
import com.zbensoft.e.payment.api.service.api.ConsumerCouponService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.CouponService;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ConsumerCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.Coupon;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumerCoupon")
@RestController
public class ConsumerCouponController {
	@Autowired
	ConsumerCouponService consumerCouponService;

	@Autowired
	CouponService couponService;
	@Autowired
	ConsumerUserClapService consumerUserClapService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	@PreAuthorize("hasRole('R_COUPON_B_Q')")
	@ApiOperation(value = "Query ConsumerCoupon，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerCoupon>> selectPage(@RequestParam(required = false) String consumerUserClapId, @RequestParam(required = false) String couponId, @RequestParam(required = false) String idNumber,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) String tradeSeq, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		ConsumerCoupon consumerCoupon = new ConsumerCoupon();
		// 必须输入一个进行查询
		if ((idNumber == null || "".equals(idNumber)) && (consumerUserClapId == null || "".equals(consumerUserClapId)) && (couponId == null || "".equals(couponId)) && (status == null || "".equals(status))) {
			return new ResponseRestEntity<List<ConsumerCoupon>>(new ArrayList<ConsumerCoupon>(), HttpRestStatus.NOT_FOUND);
		}

		if (idNumber == null || "".equals(idNumber)) {
			consumerCoupon.setConsumerUserClapId(consumerUserClapId);
		} else {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(idNumber);
			if (consumerUserClap == null) {
				return new ResponseRestEntity<List<ConsumerCoupon>>(new ArrayList<ConsumerCoupon>(), HttpRestStatus.NOT_FOUND);
			} else {
				if (consumerUserClapId == null || "".equals(consumerUserClapId)) {
					consumerCoupon.setConsumerUserClapId(consumerUserClap.getConsumerUserClapId());
				} else {
					if (consumerUserClapId.equals(consumerUserClap.getConsumerUserClapId())) {
						consumerCoupon.setConsumerUserClapId(consumerUserClapId);
					} else {
						return new ResponseRestEntity<List<ConsumerCoupon>>(new ArrayList<ConsumerCoupon>(), HttpRestStatus.NOT_FOUND);
					}
				}
			}

		}
		consumerCoupon.setCouponId(couponId);
		if (status != null) {
			consumerCoupon.setStatus(status);
		}
		consumerCoupon.setTradeSeq(tradeSeq);
		int count = consumerCouponService.count(consumerCoupon);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerCoupon>>(new ArrayList<ConsumerCoupon>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerCoupon> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerCouponService.selectPage(consumerCoupon);

		} else {
			list = consumerCouponService.selectPage(consumerCoupon);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerCoupon>>(new ArrayList<ConsumerCoupon>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerCoupon> listNew = new ArrayList<ConsumerCoupon>();
		for (ConsumerCoupon bean : list) {
			Coupon coupon = couponService.selectByPrimaryKey(bean.getCouponId());
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByPrimaryKey(bean.getConsumerUserClapId());
			if (coupon != null) {
				bean.setCouponName(coupon.getName());
				bean.setUserStartTime(DateUtil.convertDateToFormatString(coupon.getUserStartTime()));
				bean.setUserEndTime(DateUtil.convertDateToFormatString(coupon.getUserEndTime()));
			}
			if (consumerUserClap != null) {
				bean.setIdNumber(consumerUserClap.getIdNumber());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ConsumerCoupon>>(listNew, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_COUPON_B_Q')")
	@ApiOperation(value = "Query ConsumerCoupon", notes = "")
	@RequestMapping(value = "/{consumerUserClapId}/{couponId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerCoupon> selectByPrimaryKey(@PathVariable("consumerUserClapId") String consumerUserClapId, @PathVariable("couponId") String couponId) {
		ConsumerCoupon bean = new ConsumerCoupon();
		bean.setConsumerUserClapId(consumerUserClapId);
		bean.setCouponId(couponId);
		ConsumerCoupon consumerCoupon = consumerCouponService.selectByPrimaryKey(bean);
		if (consumerCoupon == null) {
			return new ResponseRestEntity<ConsumerCoupon>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumerCoupon>(consumerCoupon, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_B_E')")
	@ApiOperation(value = "Add ConsumerCoupon", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createConsumerCoupon(@Valid @RequestBody ConsumerCoupon consumerCoupon, BindingResult result, UriComponentsBuilder ucBuilder) {
		consumerCoupon.setIdNumber(CommonFun.getRelVid(consumerCoupon.getIdNumber()));
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		if ("clapId".equals(consumerCoupon.getConsumerUserClapId())) {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(consumerCoupon.getIdNumber());
			if (consumerUserClap == null) {
				return new ResponseRestEntity<Void>(HttpRestStatus.CONSUMER_NOT_FOUND);
			} else {
				consumerCoupon.setConsumerUserClapId(consumerUserClap.getConsumerUserClapId());
			}
		}

		ConsumerCoupon bean = consumerCouponService.selectByPrimaryKey(consumerCoupon);
		if (bean != null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		consumerCouponService.insert(consumerCoupon);
		// 新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerCoupon, CommonLogImpl.COUPON_MANAGE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumerCoupon/{id}").buildAndExpand(consumerCoupon.getCouponId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_COUPON_B_E')")
	@ApiOperation(value = "Edit ConsumerCoupon", notes = "")
	@RequestMapping(value = "/{consumerUserClapId}/{couponId}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerCoupon> updateConsumerCoupon(@PathVariable("consumerUserClapId") String consumerUserClapId, @PathVariable("couponId") String couponId,
			@Valid @RequestBody ConsumerCoupon consumerCoupon, BindingResult result) {
		ConsumerCoupon bean = new ConsumerCoupon();
		bean.setConsumerUserClapId(consumerUserClapId);
		bean.setCouponId(couponId);
		ConsumerCoupon currentConsumerCoupon = consumerCouponService.selectByPrimaryKey(bean);

		if (currentConsumerCoupon == null) {
			return new ResponseRestEntity<ConsumerCoupon>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentConsumerCoupon.setConsumerUserClapId(consumerCoupon.getConsumerUserClapId());
		currentConsumerCoupon.setCouponId(consumerCoupon.getCouponId());
		currentConsumerCoupon.setStatus(consumerCoupon.getStatus());
		currentConsumerCoupon.setTradeSeq(consumerCoupon.getTradeSeq());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				// System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<ConsumerCoupon>(currentConsumerCoupon, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		consumerCouponService.updateByPrimaryKey(currentConsumerCoupon);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerCoupon, CommonLogImpl.COUPON_MANAGE);
		return new ResponseRestEntity<ConsumerCoupon>(currentConsumerCoupon, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_COUPON_B_E')")
	@ApiOperation(value = "Edit Part ConsumerCoupon", notes = "")
	@RequestMapping(value = "/{consumerUserClapId}/{couponId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerCoupon> updateConsumerCouponSelective(@PathVariable("consumerUserClapId") String consumerUserClapId, @PathVariable("couponId") String couponId,
			@RequestBody ConsumerCoupon consumerCoupon) {

		ConsumerCoupon bean = new ConsumerCoupon();
		bean.setConsumerUserClapId(consumerUserClapId);
		bean.setCouponId(couponId);
		ConsumerCoupon currentConsumerCoupon = consumerCouponService.selectByPrimaryKey(bean);

		if (currentConsumerCoupon == null) {
			return new ResponseRestEntity<ConsumerCoupon>(HttpRestStatus.NOT_FOUND);
		}
		currentConsumerCoupon.setConsumerUserClapId(consumerCoupon.getConsumerUserClapId());
		currentConsumerCoupon.setCouponId(consumerCoupon.getCouponId());
		currentConsumerCoupon.setStatus(consumerCoupon.getStatus());
		currentConsumerCoupon.setTradeSeq(consumerCoupon.getTradeSeq());
		consumerCouponService.updateByPrimaryKeySelective(currentConsumerCoupon);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerCoupon, CommonLogImpl.COUPON_MANAGE);
		return new ResponseRestEntity<ConsumerCoupon>(currentConsumerCoupon, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_B_E')")
	@ApiOperation(value = "Delete ConsumerCoupon", notes = "")
	@RequestMapping(value = "/{consumerUserClapId}/{couponId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerCoupon> deleteConsumerCoupon(@PathVariable("consumerUserClapId") String consumerUserClapId, @PathVariable("couponId") String couponId) {
		ConsumerCoupon bean = new ConsumerCoupon();
		bean.setConsumerUserClapId(consumerUserClapId);
		bean.setCouponId(couponId);
		ConsumerCoupon consumerCoupon = consumerCouponService.selectByPrimaryKey(bean);
		if (consumerCoupon == null) {
			return new ResponseRestEntity<ConsumerCoupon>(HttpRestStatus.NOT_FOUND);
		}

		consumerCouponService.deleteByPrimaryKey(bean);
		// 删除日志开始
		ConsumerCoupon coupo = new ConsumerCoupon();
		coupo.setConsumerUserClapId(consumerUserClapId);
		coupo.setCouponId(couponId);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, coupo, CommonLogImpl.COUPON_MANAGE);
		// 删除日志结束
		return new ResponseRestEntity<ConsumerCoupon>(HttpRestStatus.NO_CONTENT);
	}

}