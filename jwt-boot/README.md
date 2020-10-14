# 传说中的jwt，我们来征服一下

```bash
# 本文的完整示例代码
https://github.com/yuanluoji/purestart-springboot-jwt
```

关于jwt是什么，这里就不再啰嗦了。总体来讲，它有`Header`、`Payload`、`Signature`三个部分，每一部分，都有一些细分的属性，这个原理扫一眼就可以，对我们的使用没有实质性的帮助。

使用`jwt`可以让后台服务彻底的无状态化，让登录这个过程无需session的参互，这样服务端就会获得强大的横向扩容能力，前面的`nginx`也不用配置`ip_hash`这样蛋疼的东西。

![](media/16025831079288.jpg)

我发现很多jwt的代码实例，都写的非常的复杂模糊。尤其是和SpringBoot的集成上，由于SpringSecurity的参与，这个过程更加的复杂。

本篇文章将主要介绍和SpringBoot的集成，让jwt真正走到实践中来。

首先，我们来看一看jwt的外衣是什么样子。就是下面这一长串。

```text
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5dWFubHVvamkiLCJpYXQiOjE2MDI0OTA0NDEsImV4cCI6MTYwMjQ5MjI0MX0.Qrz30s56F--cQu_fs0LWQhiZtcoLbdAuQK6dIVk4b_aSZ5is8nTs1bR7mh0qefZdiFvFk4N__sg0UouKbhH8_g
```

敏感的同学一眼就能看出来，最后一步是通过`base64`编码的。使用官方的html页面进行解码，可以看到它也仅仅是编码而已，其中的内容并没有加密。我们可以轻而易举的从Playload中，获取`yuanluoji`这样的字眼。

![](assets/images/16024917572853.jpg)

## 1. JWT使用

使用JWT，我们是期望获得登陆验证功能的，用它来替换Cookie，用它来模拟Session。大体使用流程如下：

1. 前端提交用户名和密码到任意一台服务器
2. 服务器验证用户名和密码（`spring security`或者`shiro`）
3. 如果验证成功，将使用jwt的api生成一个token
4. 这个token将会返回给前端，前端将会把它保存起来（cookie、上下文或者其他），之后每次请求，都把这个token加在http头里发送到服务端
5. 服务端能够验证token的合法性，因为里面有过期时间和防篡改机制，所以token需要完整发送

在Java中，比较流行的有两个包。一个是官方auth0的，不过好像因为使用比较复杂所以用的较少；另外一个就是jjwt。通过pom可以直接引入它。

```xml
<dependency>
	<groupId>io.jsonwebtoken</groupId>
	<artifactId>jjwt-api</artifactId>
	<version>0.11.2</version>
</dependency>
<dependency>
	<groupId>io.jsonwebtoken</groupId>
	<artifactId>jjwt-impl</artifactId>
	<version>0.11.2</version>
	<scope>runtime</scope>
</dependency>
<dependency>
	<groupId>io.jsonwebtoken</groupId>
	<artifactId>jjwt-jackson</artifactId> <!-- or jjwt-gson if Gson is preferred -->
	<version>0.11.2</version>
	<scope>runtime</scope>
</dependency>
```

先来看一下Jwt的使用方式。首先是签发token的代码，片段如下：

```java
public String generateToken(Map<String, Object> claims, String subject) {
    Date now = Calendar.getInstance().getTime();
    return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(new Date(System.currentTimeMillis() + expire * 1000))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
}
```

其中，有几个比较重要的参数需要说明一下：

- `subject` 签发的主体，比如用户名。其实它也是放在claims中的。
- `claims` 一些附加信息，也就是playload中的内容。由于它是一个HashMap，所以你大可以向里面仍你所需要的所有信息
- `expiration` 签发日期和失效日期，在验证的时候可以使用
- `secret` 加密的密钥，用来验证前面签发的内容

我们同样来看一下它的验证代码。

```java
public Claims getClaims(String token){
    Claims claims = Jwts
            .parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    return claims;
}
```

可以看到，我们同样传入了一个`secret`，如果这个secret被篡改了，那么这段代码将会抛出`SignatureException`异常。

这就是jwt的全部。大家记住这两个方法，我们的集成验证，都是基于这两个方法进行的。

#### 测试

##### 1.生成token
```http request
http://localhost:8081/user/login
POST
form-data
usernameOrEmail: 1@qq.com
password: 123456
```

```json
{
    "code": "200",
    "msg": "成功",
    "data": "bearer;eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiY29tLmxlZWxvdmVqYXZhLmJvb3Quand0LnZvLlJvbGVWb0A2Y2NjODFkNSIsInVuaXF1ZV9uYW1lIjoidGlhbmhhbyIsInVzZXJJZCI6IjEiLCJpc3MiOiJyZXN0YXBpdXNlciIsImF1ZCI6IjA5OGY2YmNkNDYyMWQzNzNjYWRlNGU4MzI2MjdiNGY2IiwiZXhwIjoxNjAyODY0ODEwLCJuYmYiOjE2MDI2OTIwMTB9.N_TzEe09UdvZaZx-xhTKmIE-j8SG_LeN0ENdje1brVM"
}
```

##### 2.验证token
```http request
http://localhost:8081/user/getTokenInfo
POST
form-data
token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiY29tLmxlZWxvdmVqYXZhLmJvb3Quand0LnZvLlJvbGVWb0A2Y2NjODFkNSIsInVuaXF1ZV9uYW1lIjoidGlhbmhhbyIsInVzZXJJZCI6IjEiLCJpc3MiOiJyZXN0YXBpdXNlciIsImF1ZCI6IjA5OGY2YmNkNDYyMWQzNzNjYWRlNGU4MzI2MjdiNGY2IiwiZXhwIjoxNjAyODY0ODEwLCJuYmYiOjE2MDI2OTIwMTB9.N_TzEe09UdvZaZx-xhTKmIE-j8SG_LeN0ENdje1brVM
```

```json
{
    "code": "200",
    "msg": "成功",
    "data": "{\"role\":\"com.leelovejava.boot.jwt.vo.RoleVo@6ccc81d5\",\"unique_name\":\"tianhao\",\"userId\":\"1\",\"iss\":\"restapiuser\",\"aud\":\"098f6bcd4621d373cade4e832627b4f6\",\"exp\":1602864810,\"nbf\":1602692010}"
}
```

**将其集成在SpringBoot项目中**

在SpringBoot体系中，使用最多的认证框架，就是亲生的Spring Security。其实jwt本身是没有什么难度的，难就难在和Spring Security的集成上，也就是Spring Security的知识更多一些。

![](assets/images/16025554664693.jpg)

如上图，我们把对Jwt使用流程，拆封成两部分。第一部分是`登录`，使用普通的Controller即可完成。第二部分是`jwt验证`，我们使用拦截器的方式去解决。

## 2.安全配置

我们使用`WebSecurityConfigurerAdapter`来完成Spring Security的配置。主要代码分3部分。

### 第一，用户数据来源

```java
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
}
```

这段代码，配置了用户数据来源，以及对于密码的摘要算法。这里采用的是安全系数较高的`BCrypt`。也就是说，我们在MySQL里保存的是BCrypt摘要的密码，SpringSecurity会根据这个算法算出我们输入密码的摘要，进行对比。

```java
@Bean
public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
 }
```

我们模拟了一个真实存在的用户数据源，下面是`JwtUserDetailsServiceImpl`的代码。意思是所有用户的密码，都是`123456`。

JwtUserDetailsServiceImpl.java

```java
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
    /**
     * 已经在 WebSecurityConfig 中生成
     */
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User(username, mockPassword(), getAuthorities());
    }

    private String mockPassword() {
        return passwordEncoder.encode("123456");
    }

    private Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        authList.add(new SimpleGrantedAuthority("ROLE_USER"));
        authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return authList;
    }
}
```

### 第二，白名单配置

我们希望有些链接，是不走SpringSecurity的拦截器的，比如swagger，比如login方法，这就需要在全局配置中进行忽略配置。

重写configure方法可以忽略某些链接。

```java
String[] SWAGGER_WHITELIST = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/swagger-resources/**",
        "/v2/api-docs",
        "/v3/api-docs",
        "/webjars/**"
};

@Override
public void configure(WebSecurity web) throws Exception {
    web.ignoring()
            .antMatchers(SWAGGER_WHITELIST)
            .antMatchers("/login**") ;
}
```

### 第三，配置过滤器

当然，里面还有一个configure方法，这次的参数是`HttpSecurity`。我们在这里添加上自定义的`JwtRequestFilter`到`UsernamePasswordAuthenticationFilter`之前。

filter一般都是责任链模式，所以会有顺序问题。

```java
@Override
protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.cors()
            .and().csrf().disable()
            .authorizeRequests()
            .antMatchers(SWAGGER_WHITELIST).authenticated()
            .anyRequest().authenticated()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().addFilterBefore(new JwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
}
```

这里要注意了。我们在`addFilterBefore`这个方法里，直接new了一个自定义的filter。经过测试，如果不这么做，把自定义filter交给Spring去管理的话，我们在上面配置的白名单会失效，这是是比较坑的地方。

到此为止，我们对SpringSecurity的配置就完成了。接下来看一下真正的登录和验证方面的代码。

## 3. 登录

登录就是一个简单的Controller。我这里使用AuthenticationManager的authenticate方法，对用户的名字和密码进行验证。验证通过之后，会调用jwt的方法，生成一个token返回。

可以看到登录的代码是非常简单的。

```java
RestController
@CrossOrigin
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTools jwtTools;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody JwtRequest jwtRequest)
            throws Exception {

        Authentication authentication = authenticate(jwtRequest.getUsername(), jwtRequest.getPassword());


        User user = User.class.cast(authentication.getPrincipal());
        final String token = jwtTools.generateToken(new HashMap<>(), user.getUsername());

        return ResponseEntity.ok(new JwtResponse(token));
    }

    private Authentication authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
```

使用swagger调用login方法，当密码输入为123456的时候，会返回我们想要的token；否则将返回403状态码。

![](assets/images/16025582888220.jpg)

## 4. 验证

验证代码主要是放在filter中，我们继承的是`OncePerRequestFilter`，可以在它的`doFilterInternal`方法里书写逻辑。

![](assets/images/16025554664693.jpg)

这部分逻辑，就是根据上面这张图进行编码的。可以看到jwt在其中，只占了很少一部分。

```java
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
    final String token = request.getHeader("Authorization");

    Claims claims = null;
    try {
        claims = getJwt().getClaims(token);
    } catch (Exception ex) {
        log.error("JWT Token error: {} , cause: {}", token, ex.getMessage());
    }

    if (claims == null) {
        chain.doFilter(request, response);
        return;
    }

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
        boolean ok = getJwt().validateTokenExpiration(claims);
        if (ok) {
            UserDetails userDetails = getUserDetailsService().loadUserByUsername(claims.getSubject());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
    chain.doFilter(request, response);
}
```

通过这种方法，就可以把jwt和springboot完美的集成起来了。在进行swagger配置的时候，我们发现了另外一个问题：需要在头信息中输入token。这可如何是好。

关于这个问题，小q也在上一篇文章中说明了。我们有两种配置方式可以完成swagger的权限令牌输入。

下面这张图，就是配置的效果。你可以参阅[《这篇文章》](https://mp.weixin.qq.com/s/PH1zc3uVwpKMIL3yW2lq1g)来完成配置。
![](assets/images/16025588086122.jpg)

## 5. 它安全么？

从我们刚开始的截图里面，就可以看出jwt的这一长串信息，是可以在客户端直接看到明文的。这就很容易让人产生怀疑，里面的内容是可以篡改的么？

我们随便复制了其中的一部分，然后使用base64解码。结果发现它果然是明文。
![](assets/images/16025593897605.jpg)

其实，如果篡改了其中的明文信息，然后使用重新使用Base64编码塞进去，是不能通过验证的。这就是我们serect密钥的作用。

在Java中，这个密钥，需要是Base64的，可以使用JDK的工具类生成。
```
String key = new String(Base64.getEncoder().encode("lk234jlk80234lsd可连接克里斯朵夫isofios23u8432ndsdfsokjjjsklfjslk%^&^&%$#$$%#83 12=12y3uiuy&^".getBytes()));
```

这个密钥可以是非常复杂的，除非它泄漏了，否则我们的jwt信息很难被篡改。所以，即使你看到的是明文，你也改不了，所以它是安全的。

## 6. jwt事件中的问题

从上面的描述中，我们可以看到，使用jwt来实现登录，是没有什么问题的。它的实现方式简单，易于扩展，可以替代cookie和session来完成登录、权限相关的功能。它的好处还有更多。首先，客户端可以用统一的方式进行处理，比如Android，IOS，Web端等。另外，jwt支持跨域。这个是相对于Cookie来说的，因为jwt可以把信息放在header里，或者直接作为参数进行传递。

问题也是有的，先说性能方面的：

- **要占带宽。** 因为你每次都需要把这个token进行传递。通常情况下，我们只在playload里存放用户id，但如果你有更多的属性比如权限等，这个串将会变的非常大。如果请求量比较高，这个开销还是挺吓人的
- **用户信息需要重复拉取** 无状态意味着服务端不会保存登录信息，这就需要每次请求都拉取一下用户信息。一般来说这些信息从数据库中拉取是不合适的，你需要一个类似redis的缓存前置。但不论怎么做，都不如session来的快捷。这样你可能会考虑堆内缓存，来模拟session的那一套。

其他使用问题。

- **token注销。**  如果是单纯的无状态，对jwt token的注销就会变的非常困难。如果token泄露，你甚至没有任何办法来防治这些不安全的请求。这是因为失效日期是写在token里的，你无法对其作出改动。
- **token续租。** 另外一个问题就是token的续租问题。比如你的token约定是在半个小时之后失效，那即使你在第29分钟进行了操作，这个token还是在约定的时间失效。这在使用人员眼里，看起来就会特别的怪异。

为了这解决这些问题，我们需要把服务`无状态`这个属性收敛一下。token生成后，在服务端保存一份（redis，nosql之类），然后再对这些token进行刷新或者注销操作。

但如果你不并不care这些问题，假定token非常安全，不会泄漏，那就可以签发一个超时时间很长的token，每次登录都生成一个，废掉之前的token。这样，那些老的token虽然存在过，但是再也没有人知道它们了。这些token此时就变成了幽灵。

## 总结

本篇文章简单的介绍了jwt，然后以springboot服务为例，看了一个集成案例。最后，讨论了jwt的优缺点。可以看到，对于一个普通的应用，jwt可以做到真正的服务解藕，拿着token这个通行证，即可穿梭在不同的系统之间。

再次提醒，本文的完整示例代码，见github仓库。

```bash
https://github.com/yuanluoji/purestart-springboot-jwt
```

如果对你有所帮助，请不要忘了为我点赞。你的支持是我创作的动力，后续会有更优质的内容分享给大家。


>很多人都假装颓废，我劝你不要上当。不要放弃每一个想要学习的念头，因为那可能是未来的你在向你求救。我是小Q，与你共进步。放弃不难，但坚持一定很酷。
