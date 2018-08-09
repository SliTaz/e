package com.zbensoft.e.payment.api.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyGroupFamilyService;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyGroupService;
import com.zbensoft.e.payment.common.util.ImportUtil;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyGroup;

import io.swagger.annotations.ApiOperation;
@RequestMapping(value = "/consumerFamilyGroup")
@RestController
public class ConsumerFamilyGroupController {
	
	
	private static final Logger log = LoggerFactory.getLogger(ConsumerFamilyGroupController.class);

	
	@Autowired
	ConsumerFamilyGroupService consumerFamilyGroupService;
	
	@Autowired
	ConsumerFamilyGroupFamilyService consumerFamilyGroupFamilyService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	@Value("${upload.file.tmp.folder}")
	private String UPLOAD_FILE_FOLDER;
	
	// 查询应用，支持分页
	@PreAuthorize("hasRole('R_BUYER_F_G_Q')")
	@ApiOperation(value = "Query consumerFamilyGroup, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerFamilyGroup>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name, 
			@RequestParam(required = false) String status, 
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		
		ConsumerFamilyGroup consumerFamilyGroup = new ConsumerFamilyGroup();
		consumerFamilyGroup.setConsumerFamilyGroupId(id);
		consumerFamilyGroup.setName(name);
		try {
			if (status == null || "".equals(status) || "undefined".equals(status)) {

			} else {
				consumerFamilyGroup.setStatus(Integer.valueOf(status));
			}

		} catch (Exception e) {
			log.error("", e);
		}
		
		int count = consumerFamilyGroupService.count(consumerFamilyGroup);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerFamilyGroup>>(new ArrayList<ConsumerFamilyGroup>(), HttpRestStatus.NOT_FOUND);
		}

		List<ConsumerFamilyGroup> list = null;// consumerRoleService.selectPage(consumeRole);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerFamilyGroupService.selectPage(consumerFamilyGroup);
		} else {
			list = consumerFamilyGroupService.selectPage(consumerFamilyGroup);
		}

		return new ResponseRestEntity<List<ConsumerFamilyGroup>>(list, HttpRestStatus.OK, count, count);
	}
	@PreAuthorize("hasRole('R_BUYER_F_G_E')")
	@ApiOperation(value = "Add consumerFamilyGroup", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createConsumerFamilyGroup(@Valid @RequestBody ConsumerFamilyGroup consumerFamilyGroup, BindingResult result, UriComponentsBuilder ucBuilder) {
		consumerFamilyGroup.setConsumerFamilyGroupId(IDGenerate.generateCommTwo(IDGenerate.CONSUMER_FAMILY_GROUP));
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
		// 是否存在相同用户名
		if (consumerFamilyGroupService.isNameExist(consumerFamilyGroup)) {//可以相同
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
         //新增
		consumerFamilyGroupService.insert(consumerFamilyGroup);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerFamilyGroup,CommonLogImpl.CONSUMER);
         //返回处理
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumerFamilyGroup/{id}").buildAndExpand(consumerFamilyGroup.getConsumerFamilyGroupId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}


	// 修改应用信息
	@PreAuthorize("hasRole('R_BUYER_F_G_E')")
	@ApiOperation(value = "Edit consumerFamilyGroup", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerFamilyGroup> updateConsumerFamilyGroup(@PathVariable("id") String id,@Valid @RequestBody ConsumerFamilyGroup consumerFamilyGroup, BindingResult result) {

		ConsumerFamilyGroup currenConsumerFamilyGroup = consumerFamilyGroupService.selectByPrimaryKey(id);

		if (currenConsumerFamilyGroup == null) {
			return new ResponseRestEntity<ConsumerFamilyGroup>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currenConsumerFamilyGroup.setName(consumerFamilyGroup.getName());
		currenConsumerFamilyGroup.setStatus(consumerFamilyGroup.getStatus());
		currenConsumerFamilyGroup.setRemark(consumerFamilyGroup.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			/*for (ObjectError error : list) {
				System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}*/

			return new ResponseRestEntity<ConsumerFamilyGroup>(currenConsumerFamilyGroup, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		consumerFamilyGroupService.updateByPrimaryKey(currenConsumerFamilyGroup);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currenConsumerFamilyGroup,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerFamilyGroup>(currenConsumerFamilyGroup, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分应用信息
	@PreAuthorize("hasRole('R_BUYER_F_G_E')")
	@ApiOperation(value = "Edit Part consumerFamilyGroup", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerFamilyGroup> updateConsumerFamilyGroupSelective(@PathVariable("id") String id, @RequestBody ConsumerFamilyGroup consumerFamilyGroup) {

		ConsumerFamilyGroup currenConsumerFamilyGroup = consumerFamilyGroupService.selectByPrimaryKey(id);

		if (currenConsumerFamilyGroup == null) {
			return new ResponseRestEntity<ConsumerFamilyGroup>(HttpRestStatus.NOT_FOUND);
		}
		consumerFamilyGroup.setConsumerFamilyGroupId(id);
		consumerFamilyGroupService.updateByPrimaryKeySelective(consumerFamilyGroup);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, consumerFamilyGroup,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerFamilyGroup>(currenConsumerFamilyGroup, HttpRestStatus.OK);
	}

	// 删除指定应用
	@PreAuthorize("hasRole('R_BUYER_F_G_E')")
	@ApiOperation(value = "Delete consumerFamilyGroup", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerFamilyGroup> deleteConsumerFamilyGroup(@PathVariable("id") String id) {

		ConsumerFamilyGroup consumerFamilyGroup = consumerFamilyGroupService.selectByPrimaryKey(id);
		if (consumerFamilyGroup == null) {
			return new ResponseRestEntity<ConsumerFamilyGroup>(HttpRestStatus.NOT_FOUND);
		}
		
		//删除中间表
		consumerFamilyGroupFamilyService.deleteByGroupId(id);

		consumerFamilyGroupService.deleteByPrimaryKey(id);
		//删除日志开始
		ConsumerFamilyGroup delBean = new ConsumerFamilyGroup();
		delBean.setConsumerFamilyGroupId(id);
	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.CONSUMER);
	//删除日志结束
		return new ResponseRestEntity<ConsumerFamilyGroup>(HttpRestStatus.NO_CONTENT);
	}

	//启用
	@PreAuthorize("hasRole('R_BUYER_F_G_E')")
	@ApiOperation(value = "statusEnable", notes = "")
	@RequestMapping(value = "/statusEnable", method = RequestMethod.GET)
	public ResponseRestEntity<Void> statusEnable(@RequestParam(required = false) String id) {
		consumerFamilyGroupService.statusEnable(id);
		//修改日志
				ConsumerFamilyGroup bean =new ConsumerFamilyGroup();
				bean.setConsumerFamilyGroupId(id);
				bean.setStatus(MessageDef.STATUS.ENABLE_INT);
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, bean,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<Void>(HttpRestStatus.OK);
	}
		
	//停用
	@PreAuthorize("hasRole('R_BUYER_F_G_E')")
	@ApiOperation(value = "statusDisable", notes = "")
	@RequestMapping(value = "/statusDisable", method = RequestMethod.GET)
	public ResponseRestEntity<Void> statusDisable(@RequestParam(required = false) String id) {
		consumerFamilyGroupService.statusDisable(id);
		//修改日志
		ConsumerFamilyGroup bean =new ConsumerFamilyGroup();
				bean.setConsumerFamilyGroupId(id);
				bean.setStatus(MessageDef.STATUS.DISABLE_INT);
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, bean,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<Void>(HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BUYER_F_G_E')")
	@RequestMapping(value="/singleUpload", method = RequestMethod.POST)
	public Map<String,Object> singleFileUpload(HttpServletRequest request,@RequestParam("name") String name,
			@RequestParam("consumerFamilyGroupId") String consumerFamilyGroupId,@RequestParam("flag") String flag) throws Exception {
		//System.out.println("flag:"+flag);//true表示删除之前全部数据，false表示不删除之前全部数据
		
		boolean deleteFlagAll = false;

		if (!CommonFun.isEmpty(flag)) {
			if (flag.equals("true")) {
				deleteFlagAll = true;
			}
		}
		
		String filePre=System.currentTimeMillis()+"";
		name=filePre+"_"+name;
		
		//System.out.println("after name:"+name);
		//System.out.println("consumerFamilyGroupId:"+consumerFamilyGroupId);
		
		String path = UPLOAD_FILE_FOLDER;//request.getSession().getServletContext().getRealPath("upload"); 
		//System.out.println("path:"+path);
		
        File targetFile = new File(path, name);
        if(!targetFile.getParentFile().exists()){  
            targetFile.getParentFile().mkdirs();  
        }
        
        write(path,name,request.getInputStream());
        
        int import_int=insertToDB(targetFile,consumerFamilyGroupId,deleteFlagAll);
        
        Map<String,Object> result_map=new HashMap<String,Object>();
        result_map.put("importNum",import_int);
		return result_map;
	}
	
	private void write(String path,String filename, InputStream in) {
		//System.out.println("写入文件");

		File file = new File(path);
		if (!file.exists()) {
			if (!file.mkdirs()) {// 若创建文件夹不成功
				//System.out.println("Unable to create external cache directory");
			}
		}

		File targetfile = new File(path + filename);
		OutputStream os = null;
		try {
			os = new FileOutputStream(targetfile);
			int ch = 0;
			while ((ch = in.read()) != -1) {
				os.write(ch);
			}
			os.flush();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				os.close();
				in.close();
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}
	
	
	private int insertToDB(File file,String consumerFamilyGroupId,boolean deleteFlagAll){
		//System.out.println("准备写入数据库中...字符集");
		try {
			//BufferedReader in =new BufferedReader(new FileReader(file));
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader in = new BufferedReader(isr);
			List<String> list = new ArrayList<String>();
			String s;
			int line_num=0;
			while((s=in.readLine())!=null){
				line_num=line_num+1;
				String[] str = s.split(",");
				if(str!=null){
					String familyId = null;
					if(str.length==1){
						familyId = str[0];
						
						if(!CommonFun.isEmpty(familyId)){
							familyId=familyId.trim();
							
							//为了解决txt中的第一行的问题 start
							//例如第一行数据是1按照常规来看长度应该是1但是长度显示是2 故需要去除第一位得到后面的数据才是真实的数据
							if(line_num==1&&familyId.length()>=2){
								familyId=ImportUtil.getValueForUTF_8(familyId);
							}
							//为了解决txt中的第一行的问题 end
							
							list.add(familyId.trim());
						}
						
					}
				}
			}
			//System.out.println("条数："+list.size());
			int import_int=consumerFamilyGroupService.saveOrUpdate(list,consumerFamilyGroupId,deleteFlagAll);
			CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_IMPORT, list,CommonLogImpl.CONSUMER);
			return import_int;
		} catch (Exception e) {
			log.error("", e);
		}
		
		return 0;
	}
}
