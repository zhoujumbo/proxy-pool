package com.basic.support.commons.business.crud.controller;

import com.basic.support.commons.business.crud.dao.CrudDao;
import com.basic.support.commons.business.crud.pojo.BasePojo;
import com.basic.support.commons.business.crud.bo.CrudBo;

public class CrudController<Pojo extends BasePojo, Bo extends CrudBo<Pojo, ? extends CrudDao<Pojo>>>
		extends SchController<Pojo, Bo> {

}
