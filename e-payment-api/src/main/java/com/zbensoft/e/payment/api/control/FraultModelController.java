package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
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
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.FraultModelProcessService;
import com.zbensoft.e.payment.api.service.api.FraultModelService;
import com.zbensoft.e.payment.db.domain.FraultModel;
import com.zbensoft.e.payment.db.domain.FraultModelProcess;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultModel")
@RestController
public class FraultModelController {
	@Autowired
	FraultModelService fraultModelService;
	
	@Autowired
	FraultModelProcessService fraultModelProcessService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query FraultModel，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultModel>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String createTimeStart,
			@RequestParam(required = false) String createTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		FraultModel fraultModel = new FraultModel();
		fraultModel.setFraultModelId(id);
		fraultModel.setName(name);
		fraultModel.setStatus(status);
		fraultModel.setCreateTimeStart(createTimeStart);
		fraultModel.setCreateTimeEnd(createTimeEnd);
		int count = fraultModelService.count(fraultModel);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultModel>>(new ArrayList<FraultModel>(), HttpRestStatus.NOT_FOUND);
		}
		List<FraultModel> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultModelService.selectPage(fraultModel);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = fraultModelService.selectPage(fraultModel);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultModel>>(new ArrayList<FraultModel>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<FraultModel>>(list, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query FraultModel", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultModel> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultModel fraultModel = fraultModelService.selectByPrimaryKey(id);
		if (fraultModel == null) {
			return new ResponseRestEntity<FraultModel>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultModel>(fraultModel, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultModel", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createFraultModel(@Valid @RequestBody FraultModel fraultModel,BindingResult result, UriComponentsBuilder ucBuilder) {

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultModel.setFraultModelId(IDGenerate.generateCommTwo(IDGenerate.FRAULT_MODEL));
		fraultModel.setCreateTime(PageHelperUtil.getCurrentDate());
		fraultModelService.insert(fraultModel);
		
		//关系表新增Start
		if(fraultModel.getFraultProcessId()!=null){
			String[] idStr = fraultModel.getFraultProcessId().split(",");
			for(int i=0;i<idStr.length;i++){
				FraultModelProcess  fraultModelProcess =new FraultModelProcess();
				fraultModelProcess.setFraultModelId(fraultModel.getFraultModelId());
				fraultModelProcess.setFraultProcessId(idStr[i]);
				fraultModelProcessService.insert(fraultModelProcess);
			}
		}
		//关系表新增End
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, fraultModel,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/fraultModel/{id}").buildAndExpand(fraultModel.getFraultModelId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultModel", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultModel> updateFraultModel(@PathVariable("id") String id,@Valid @RequestBody FraultModel fraultModel, BindingResult result) {

		FraultModel currentFraultModel = fraultModelService.selectByPrimaryKey(id);
		List<FraultModelProcess> fraultModelProcessList =fraultModelProcessService.selectByModelId(id);

		if (currentFraultModel == null) {
			return new ResponseRestEntity<FraultModel>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentFraultModel.setName(fraultModel.getName());
		currentFraultModel.setStatus(fraultModel.getStatus());
		currentFraultModel.setHandleClass(fraultModel.getHandleClass());
		currentFraultModel.setCreateTime(fraultModel.getCreateTime());
		currentFraultModel.setRemark(fraultModel.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<FraultModel>(currentFraultModel,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultModelService.updateByPrimaryKey(currentFraultModel);
		
		//关系表修改start(逻辑:先删除，然后增加)
		if (fraultModelProcessList != null && fraultModelProcessList.size() > 0) {
			for(FraultModelProcess gateway :fraultModelProcessList){
				fraultModelProcessService.deleteByPrimaryKey(gateway);
			}
		}
		
		if(fraultModel.getFraultProcessId()!=null&&!"".equals(fraultModel.getFraultProcessId())){
			String[] idStr = fraultModel.getFraultProcessId().split(",");
			for(int i=0;i<idStr.length;i++){
				FraultModelProcess  fraultModelProcess =new FraultModelProcess();
				fraultModelProcess.setFraultModelId(fraultModel.getFraultModelId());
				fraultModelProcess.setFraultProcessId(idStr[i]);
				fraultModelProcessService.insert(fraultModelProcess);
			}
		}
		//关系表修改start(逻辑:先删除，然后增加)
		//修改日志
     CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultModel,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultModel>(currentFraultModel, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part FraultModel", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultModel> updateFraultModelSelective(@PathVariable("id") String id,
			@RequestBody FraultModel fraultModel) {

		FraultModel currentFraultModel = fraultModelService.selectByPrimaryKey(id);

		if (currentFraultModel == null) {
			return new ResponseRestEntity<FraultModel>(HttpRestStatus.NOT_FOUND);
		}
		currentFraultModel.setFraultModelId(id);
		currentFraultModel.setName(fraultModel.getName());
		currentFraultModel.setStatus(fraultModel.getStatus());
		currentFraultModel.setHandleClass(fraultModel.getHandleClass());
		currentFraultModel.setCreateTime(fraultModel.getCreateTime());
		currentFraultModel.setRemark(fraultModel.getRemark());
		fraultModelService.updateByPrimaryKeySelective(currentFraultModel);
		//修改日志
	     CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultModel,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultModel>(currentFraultModel, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete FraultModel", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultModel> deleteFraultModel(@PathVariable("id") String id) {

		FraultModel fraultModel = fraultModelService.selectByPrimaryKey(id);
		List<FraultModelProcess> fraultModelProcessList =fraultModelProcessService.selectByModelId(id);
		if (fraultModel == null) {
			return new ResponseRestEntity<FraultModel>(HttpRestStatus.NOT_FOUND);
		}

		fraultModelService.deleteByPrimaryKey(id);
		
		//关系表删除start
		if (fraultModelProcessList != null && fraultModelProcessList.size()>0){
			for(FraultModelProcess fraultModelProcess :fraultModelProcessList){
				fraultModelProcessService.deleteByPrimaryKey(fraultModelProcess);
			}
		}
		//关系表删除end
		//删除日志开始
		FraultModel delBean = new FraultModel();
		delBean.setFraultModelId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultModel>(HttpRestStatus.NO_CONTENT);
	}

}