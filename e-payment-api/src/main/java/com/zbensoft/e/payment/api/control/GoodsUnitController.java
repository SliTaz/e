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
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.GoodsUnitService;
import com.zbensoft.e.payment.db.domain.GoodsUnit;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/goodsUnit")
@RestController
public class GoodsUnitController {
	@Autowired
	GoodsUnitService goodsUnitService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_COUPON_GU_Q')")
	@ApiOperation(value = "Query GoodsUnit，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<GoodsUnit>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		GoodsUnit goodsUnit = new GoodsUnit();
		goodsUnit.setGoodUnitId(id);
		goodsUnit.setName(name);
		goodsUnit.setDeleteFlag(0);
		int count = goodsUnitService.count(goodsUnit);
		if (count == 0) {
			return new ResponseRestEntity<List<GoodsUnit>>(new ArrayList<GoodsUnit>(), HttpRestStatus.NOT_FOUND);
		}
		List<GoodsUnit> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = goodsUnitService.selectPage(goodsUnit);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = goodsUnitService.selectPage(goodsUnit);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<GoodsUnit>>(new ArrayList<GoodsUnit>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<GoodsUnit>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_COUPON_GU_Q')")
	@ApiOperation(value = "Query GoodsUnit", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<GoodsUnit> selectByPrimaryKey(@PathVariable("id") String id) {
		GoodsUnit goodsUnit = goodsUnitService.selectByPrimaryKey(id);
		if (goodsUnit == null) {
			return new ResponseRestEntity<GoodsUnit>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<GoodsUnit>(goodsUnit, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_GU_E')")
	@ApiOperation(value = "Add GoodsUnit", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createGoodsUnit(@Valid @RequestBody GoodsUnit goodsUnit, BindingResult result, UriComponentsBuilder ucBuilder) {
		//goodsUnit.setGoodUnitId(System.currentTimeMillis()+"");
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		goodsUnit.setDeleteFlag(0);
		GoodsUnit bean = goodsUnitService.selectByPrimaryKey(goodsUnit.getGoodUnitId());
		if (goodsUnitService.isGoodsUnitExist(goodsUnit) || bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		
		goodsUnitService.insert(goodsUnit);
		//新增日志
       //CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, goodsUnit,CommonLogImpl.SYSTEM_MANAGE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/goodsUnit/{id}").buildAndExpand(goodsUnit.getGoodUnitId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_COUPON_GU_E')")
	@ApiOperation(value = "Edit GoodsUnit", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<GoodsUnit> updateGoodsUnit(@PathVariable("id") String id, @Valid @RequestBody GoodsUnit goodsUnit, BindingResult result) {

		GoodsUnit currentGoodsUnit = goodsUnitService.selectByPrimaryKey(id);

		if (currentGoodsUnit == null) {
			return new ResponseRestEntity<GoodsUnit>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentGoodsUnit.setName(goodsUnit.getName());
		currentGoodsUnit.setDeleteFlag(goodsUnit.getDeleteFlag());
		currentGoodsUnit.setRemark(goodsUnit.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<GoodsUnit>(currentGoodsUnit,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		goodsUnitService.updateByPrimaryKey(currentGoodsUnit);
		//修改日志
       // CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentGoodsUnit,CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<GoodsUnit>(currentGoodsUnit, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_COUPON_GU_E')")
	@ApiOperation(value = "Edit Part GoodsUnit", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<GoodsUnit> updateGoodsUnitSelective(@PathVariable("id") String id, @RequestBody GoodsUnit goodsUnit) {

		GoodsUnit currentGoodsUnit = goodsUnitService.selectByPrimaryKey(id);

		if (currentGoodsUnit == null) {
			return new ResponseRestEntity<GoodsUnit>(HttpRestStatus.NOT_FOUND);
		}
		currentGoodsUnit.setGoodUnitId(id);
		currentGoodsUnit.setName(goodsUnit.getName());
		currentGoodsUnit.setDeleteFlag(goodsUnit.getDeleteFlag());
		currentGoodsUnit.setRemark(goodsUnit.getRemark());
		goodsUnitService.updateByPrimaryKeySelective(currentGoodsUnit);
		//修改日志
	   //CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentGoodsUnit,CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<GoodsUnit>(currentGoodsUnit, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_COUPON_GU_E')")
	@ApiOperation(value = "Delete GoodsUnit", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<GoodsUnit> deleteGoodsUnit(@PathVariable("id") String id) {

		GoodsUnit goodsUnit = goodsUnitService.selectByPrimaryKey(id);
		if (goodsUnit == null) {
			return new ResponseRestEntity<GoodsUnit>(HttpRestStatus.NOT_FOUND);
		}
		goodsUnit.setDeleteFlag(1);
		goodsUnitService.updateByPrimaryKeySelective(goodsUnit);
		//删除日志开始
//		GoodsUnit delBean = new GoodsUnit();
//		delBean.setGoodUnitId(id);
//		delBean.setDeleteFlag(1);
//		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.SYSTEM_MANAGE);
		//删除日志结束
		return new ResponseRestEntity<GoodsUnit>(HttpRestStatus.NO_CONTENT);
	}

}