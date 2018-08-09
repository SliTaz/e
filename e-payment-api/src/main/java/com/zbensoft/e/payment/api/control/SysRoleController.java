package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.SysMenuService;
import com.zbensoft.e.payment.api.service.api.SysRoleService;
import com.zbensoft.e.payment.db.domain.SysMenu;
import com.zbensoft.e.payment.db.domain.SysRole;
import com.zbensoft.e.payment.db.domain.ZTreeNode;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/role")
@RestController
public class SysRoleController {
	
	private static final Logger log = LoggerFactory.getLogger(SysRoleController.class);

	@Autowired
	SysRoleService sysRoleService;
	
	@Autowired
	SysMenuService sysMenuService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;


	@PreAuthorize("hasRole('R_MANAG_U_R_Q')")
	@ApiOperation(value = "Query SysRole,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SysRole>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name, @RequestParam(required = false) String roleId,
			@RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		SysRole role = new SysRole();
		role.setRoleId(id);
		role.setName(name);

		role.setRemark(remark);

		int count = sysRoleService.count(role);
		if (count == 0) {
			return new ResponseRestEntity<List<SysRole>>(new ArrayList<SysRole>(), HttpRestStatus.NOT_FOUND);
		}

		List<SysRole> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = sysRoleService.selectPage(role);
		} else {
			list = sysRoleService.selectPage(role);
		}

		return new ResponseRestEntity<List<SysRole>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_Q')")
	@ApiOperation(value = "Query SysRole", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<SysRole> selectByPrimaryKey(@PathVariable("id") String id) {
		SysRole sysRole = sysRoleService.selectByPrimaryKey(id);
		if (sysRole == null) {
			return new ResponseRestEntity<SysRole>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<SysRole>(sysRole, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_E')")
	@ApiOperation(value = "Add SysRole", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createRole(@RequestBody SysRole role,BindingResult result,  UriComponentsBuilder ucBuilder) {
		//role.setRoleId(System.currentTimeMillis() + "");
		role.setRoleId(IDGenerate.generateCommOne(IDGenerate.SYS_USR_ROLE));
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
		if (sysRoleService.isRoleExist(role)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		sysRoleService.insert(role);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, role,CommonLogImpl.MANAGE_USER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/role/{id}").buildAndExpand(role.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_E')")
	@ApiOperation(value = "Edit SysRole", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysRole> updateRole(@PathVariable("id") String id, @RequestBody SysRole role) {

		SysRole currentRole = sysRoleService.selectByPrimaryKey(id);

		if (currentRole == null) {
			return new ResponseRestEntity<SysRole>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentRole.setRoleId(role.getRoleId());
		currentRole.setCode(role.getCode());
		currentRole.setName(role.getName());
		currentRole.setRemark(role.getRemark());
		sysRoleService.updateByPrimaryKey(currentRole);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentRole,CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysRole>(currentRole, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_E')")
	@ApiOperation(value = "Edit Part SysRole", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<SysRole> updateRoleSelective(@PathVariable("id") String id, @RequestBody SysRole role) {

		SysRole currentRole = sysRoleService.selectByPrimaryKey(id);

		if (currentRole == null) {
			return new ResponseRestEntity<SysRole>(HttpRestStatus.NOT_FOUND);
		}
		sysRoleService.updateByPrimaryKeySelective(role);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, role,CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysRole>(currentRole, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_E')")
	@ApiOperation(value = "Delete SysRole", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysRole> deleteRole(@PathVariable("id") String id) {

		SysRole role = sysRoleService.selectByPrimaryKey(id);
		if (role == null) {
			return new ResponseRestEntity<SysRole>(HttpRestStatus.NOT_FOUND);
		}

		sysRoleService.deleteByPrimaryKey(id);
		//删除日志开始
		SysRole sys = new SysRole();
						sys.setRoleId(id);
				    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, sys,CommonLogImpl.MANAGE_USER);
					//删除日志结束
		return new ResponseRestEntity<SysRole>(HttpRestStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_Q')")
	@RequestMapping(value = "/permissioRole", method = RequestMethod.GET)
	public ResponseEntity<SysRole> permissioRole(@RequestParam(required = false) String id) {
		//System.out.println("id:"+id);
		List<SysMenu> resources = sysMenuService.getRoleResources(id);
		//System.out.println("resources 条数:"+resources.size());
		
		List<SysMenu> allRes = sysMenuService.findAll();
		//System.out.println("allRes 条数:"+allRes.size());
		
		StringBuffer sb = new StringBuffer();
        sb.append("var zNodes = [];");
        
        List<ZTreeNode> zTreeNodeList=new ArrayList<ZTreeNode>();
        
        for (SysMenu r : allRes) {
            boolean flag = false;
            for (SysMenu ur : resources) {//用户拥有的权限
                if (ur.getMenuId().equals(r.getMenuId())) {
                	ZTreeNode zTreeNode=new ZTreeNode();
                	
                    sb.append("zNodes.push({ id: '"
                            + r.getMenuId() + "', pId: '"
                            + r.getPreMenuId()
                            + "', name: '" + r.getMenuNameCn()+","+r.getMenuNameEn()+","+r.getMenuNameEs()
                            + "',checked: true});");//ztree勾选状态
                    flag = true;
                    
                    zTreeNode.setId(r.getMenuId());
                    zTreeNode.setpId(r.getPreMenuId());
                    zTreeNode.setName(r.getMenuNameCn()+","+r.getMenuNameEn()+","+r.getMenuNameEs());
                    zTreeNode.setChecked(true);
                    zTreeNodeList.add(zTreeNode);
                }
            }
            if (!flag) {
                sb.append("zNodes.push({ id: '"
                        + r.getMenuId() + "', pId: '"
                        + r.getPreMenuId()
                        + "', name: '" + r.getMenuNameCn()+","+r.getMenuNameEn()+","+r.getMenuNameEs()
                        + "',checked: false});");//ztree不勾选状态
                
                ZTreeNode zTreeNode=new ZTreeNode();
                zTreeNode.setId(r.getMenuId());
                zTreeNode.setpId(r.getPreMenuId());
                zTreeNode.setName(r.getMenuNameCn()+","+r.getMenuNameEn()+","+r.getMenuNameEs());
                zTreeNode.setChecked(false);
                zTreeNodeList.add(zTreeNode);

            }
        }
        
        //System.out.println("resources:"+sb);
        
        //Map<String, Object> modelMap=new HashMap<String, Object>();
        //modelMap.put("roleId", id);
        //modelMap.put("resources", sb);
        //System.out.println("测试了是否正常路径11");
        //ModelAndView mv = new ModelAndView("http://localhost:9090/permissioRole.html",modelMap);
        
        //return mv;
        
        SysRole role=new SysRole();
        role.setRoleId(id);
        role.setzTreeNodes(zTreeNodeList);
        
        return new ResponseEntity<SysRole>(role,HttpStatus.OK);
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_E')")
	@RequestMapping(value = "/saveRoleRescoursForRole", method = RequestMethod.POST)
	public ResponseRestEntity<Void> saveRoleRescoursForRole(@RequestParam(required = false) String roleId,
			@RequestParam(required = false) String rescId) {
		//System.out.println("roleId:"+roleId+";rescId:"+rescId);
		
		List<String> list = CommonFun.removeSameItem(Arrays.asList(rescId.split(",")));
		
		try {
			sysMenuService.saveRoleRescours(roleId, list);
		} catch (Exception e) {
			log.error("",e);
			return new ResponseRestEntity<Void>(HttpRestStatus.UNKNOWN,localeMessageSourceService.getMessage("common.failed"));
		}
		return new ResponseRestEntity<Void>(HttpRestStatus.OK,localeMessageSourceService.getMessage("common.success"));
		
	}

}