# swagger2

- [Swagger规范RESTful API 设计指南](http://blog.720ui.com/2016/restful_swagger_api/)
- [良好的 API 设计指南](http://blog.720ui.com/2017/restful_api/)
- [SpringBoot使用Swagger2实现Restful API,加在model](https://www.dalaoyang.cn/article/21)




[swagger注释API详细说明](https://blog.csdn.net/xupeng874395012/article/details/68946676)

@ApiImplicitParam
属性	            取值	        作用
paramType		 /       查询参数类型
                path	 以地址的形式提交数据
                query	直接跟参数完成自动映射赋值
                body	以流的形式提交 仅支持POST
                header	参数在request headers 里边提交
                form	以form表单的形式提交 仅支持POST
                
dataType		/       参数的数据类型 只作为标志说明，并没有实际验证
                Long	
                String	
name		    /       接收参数名
value		    /       接收参数的意义描述

required		/       参数是否必填
                true	必填
                false	非必填
                
defaultValue		默认值