
# zoom-web

zoom-web是一个小型的mvc框架。


# Controller控制器


## 路由

#### @Controller

zoom 约定，所有Controller必须在controllers这个包名下面，并且需要标注@Controller(

```java

@Controller(key="/")
class MyController{

    @Mappding("")
    public String index(){
        return "hello world";
    }


    public String sayHello(){
       return "sayHello";
    }
}

```

`Controller#key` 和 @Mapping 或 方法名称共同确定一个url的处理路径，计算方法为：

```
key + (方法名称 或者 Mapping.value())
```

上面的Controller创建了两个url

```
/
/sayHello
```



#### @Mapping

这个标注的value属性指定了本方法映射的路径，method属性指定了本方法能够处理的HttpMethod，如果未指定，则表示能处理所有HttpMethod

HttpMethod包含如下几种:
+ GET
+ POST
+ PUT
+ DELETE
+ PATCH
+ OPTIONS

在一个Mapping中可以指定多个支持的HttpMethod

```java

@Controller(key="/")
class MyController{

    @Mappding(value="",method={Mapping.GET.Mapping.POST})
    public String index(){
        return "hello world";
    }

}

```

上面的`Mappding`表示这个方法仅仅接收 GET和POST方法。

#### PathVariable路径参数



```java

@Controller(key="/")
class MyController{


     // 标注 pathVariable=true,表示本参数来自url路径
    @Mappding(value="user/{id}",method={Mapping.GET.Mapping.POST})
    public String index(
        @Param(pathVariable=true) String id
    ){
        return "hello world";
    }

}

```

上面的路由中含有参数 {id}, 表示该方法接收参数化url

```
/user/1
/user/2
/user/3
```
都可以访问到该方法,而方法参数中指定了 `@Param(pathVariable=true) String id`,表示该参数是来自url路径中的参数。


#### 方法参数

```
@Controller(key="/")
class MyController{

    @Mappding(value="user/{id}",method={Mapping.GET.Mapping.POST})
    public String index(

        // 标注 pathVariable=true,表示本参数来自url路径
        @Param(pathVariable=true) String id,

        //标注 body=true,表示这个参数将会解析整个的Http Body
        @Param(body=true) Map<String,Object> body,

        // 没有标注，表示这是一个普通的参数，将从Http Body中提取某个属性
        String name,

        // 没有标注，表示这是一个普通的参数，将从Http Body中提取某个属性
        int age
    ){
        return "hello world";
    }

}

```


zoom对Http参数的解析，如表单提交、文件提交和json数据提交等，依赖提交时候设置的Http Header。

+ json
对于Content-Type=application/json,将会认为整个Http Body是一个json字符串。方法中的参数从json字符串解析的map中提取。

+ 文件
对于Content-Type=multipart/form-data将会认为这是一次表单中含文件域的提交。方法中的参数将从提交的表单域提取。

+ form
如果Content-Type为其他，则不会做特殊处理，认为是一次普通提交。方法中的参数将从HttpServletRequest.getParameter或者HttpServletRequest.getParametes方法提取。



## View

#### json输出

在zoom-web中进行json输出只需要在方法上标注@JsonResponse

```java

@Controller(key="/")
class MyController{

    // 标注Json Response，表示
    @JsonResponse
    public String index(
    ){
        return "hello world";
    }

}
```

#### 模板输出


```java

@Controller(key="/")
class MyController{

    public String index(
    ){
        // 没有标注JsonResponse，表示这是一个模板输出，将会使用user/index这个模板来进行渲染。
        return "user/index";
    }

}
```


+ 模板位置
默认模板位置为WEB-INF/templates下,上面的Controller在处理 /index 这个路由的时候，将会使用 `WEB-INF/templates/user/index.html` 来渲染页面。

+ 模板数据
zoom-web渲染模板的数据来源有两块，一个是HttpServletRequest.getAttribute,另一个是`ActionContext.getData`


```java

@Controller(key="/")
class MyController{

    public String index(
        ActionContext context,
        HttpServletRequest request
    ){
        // 这里设置的数据将被渲染到页面
        context.set("id",1);
        // 这里设置的数据将被渲染到页面
        request.setAttribute("name","张三");

        // 没有标注JsonResponse，表示这是一个模板输出，将会使用user/index这个模板来进行渲染。
        return "user/index";
    }

}
```


## Model



## Action拦截器










