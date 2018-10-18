package com.leelovejava.controller

import com.leelovejava.domain.MetaTable
import com.leelovejava.service.MetaTableService
import com.leelovejava.utils.ResultVOUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation._


@RestController
@RequestMapping(Array("/meta/table"))
class MetaTableController @Autowired()(metaTableService: MetaTableService) {

  @RequestMapping(value = Array("/"), method = Array(RequestMethod.POST))
  @ResponseBody
  def save(@ModelAttribute metaTable:MetaTable) = {
    metaTableService.save(metaTable)
    ResultVOUtil.success()  // 此处就是Scala调用已有的Java代码
  }

  /**
    * 查询
    * http://127.0.0.1:7777/scala-boot/meta/table/
    * @return
    */
  @RequestMapping(value = Array("/"), method = Array(RequestMethod.GET))
  @ResponseBody
  def query() = {
    ResultVOUtil.success(metaTableService.query())
  }

}
