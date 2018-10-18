package com.leelovejava.controller

import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}


@RestController
class ScalaHelloBoot {

  /**
    * http://127.0.0.1:7777/scala-boot/sayScalaHello
    * @return
    */
  @RequestMapping(value = Array("/sayScalaHello"), method = Array(RequestMethod.GET))
  def sayScalaHello() = {

    "Hello Scala Boot...."
  }

}
