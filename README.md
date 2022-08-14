# APP组件化演化
## 康威定律：
"设计系统的架构受制于产生这些设计的组织的沟通结构。"
## 组件化演化
基础模块化->模块化->插件化->进程化
## 设计考虑：
项目功能特点，公司开发人员等
## 整体设计：
* 复用：基础服务，基础组件复用，有差异化组件的复用
* 代码隔离，用project-module-pin来进行代码隔离

## 层次设计划分
## 代码设计
### 生命周期的分发
Lifecycle
### 组件化通信
消息总线可参考我实现的[https://github.com/chaoyueLin/cevnetbus](https://github.com/chaoyueLin/cevnetbus)
### 路由
服务调用Iproider，SPI，全称Service Provider Interfaces，服务提供接口。是Java提供的一套供第三方实现或扩展使用的技术体系。解耦了服务提供与服务使用。
Arouter整体代码设计都是一句这个思想
#### [Arouter](https://github.com/alibaba/ARouter)
![](./img/arouter.png)


		class Warehouse {
		    // Cache route and metas
		    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
		    static Map<String, RouteMeta> routes = new HashMap<>();
		
		    // Cache provider
		    static Map<Class, IProvider> providers = new HashMap<>();
		    static Map<String, RouteMeta> providersIndex = new HashMap<>();
		
		    // Cache interceptor
		    static Map<Integer, Class<? extends IInterceptor>> interceptorsIndex = new UniqueKeyTreeMap<>("More than one interceptors use same priority [%s]");
		    static List<IInterceptor> interceptors = new ArrayList<>();
		
		    static void clear() {
		        routes.clear();
		        groupsIndex.clear();
		        providers.clear();
		        providersIndex.clear();
		        interceptors.clear();
		        interceptorsIndex.clear();
		    }
		}


* 路由原理，生成路由表Group表。反射构造实例。获取里面的路由添加到warehouse的map,ARouter$$Root$$(moduleName),ARouter$$Group$$(path)

		


		public class ARouter$$Root$$app implements IRouteRoot {
		  @Override
		  public void loadInto(Map<String, Class<? extends IRouteGroup>> routes) {
		    routes.put("test", ARouter$$Group$$test.class);
		    routes.put("yourservicegroupname", ARouter$$Group$$yourservicegroupname.class);
		  }
		}


		public class ARouter$$Group$$test implements IRouteGroup {
		  @Override
		  public void loadInto(Map<String, RouteMeta> atlas) {
		    atlas.put("/test/activity1", RouteMeta.build(RouteType.ACTIVITY, Test1Activity.class, "/test/activity1", "test", new java.util.HashMap<String, Integer>(){{put("ser", 9); put("ch", 5); put("fl", 6); put("dou", 7); put("boy", 0); put("url", 8); put("pac", 10); put("obj", 11); put("name", 8); put("objList", 11); put("map", 11); put("age", 3); put("height", 3); }}, -1, -2147483648));
		    atlas.put("/test/activity2", RouteMeta.build(RouteType.ACTIVITY, Test2Activity.class, "/test/activity2", "test", new java.util.HashMap<String, Integer>(){{put("key1", 8); }}, -1, -2147483648));
		    atlas.put("/test/activity3", RouteMeta.build(RouteType.ACTIVITY, Test3Activity.class, "/test/activity3", "test", new java.util.HashMap<String, Integer>(){{put("name", 8); put("boy", 0); put("age", 3); }}, -1, -2147483648));
		    atlas.put("/test/activity4", RouteMeta.build(RouteType.ACTIVITY, Test4Activity.class, "/test/activity4", "test", null, -1, -2147483648));
		    atlas.put("/test/fragment", RouteMeta.build(RouteType.FRAGMENT, BlankFragment.class, "/test/fragment", "test", null, -1, -2147483648));
		    atlas.put("/test/webview", RouteMeta.build(RouteType.ACTIVITY, TestWebview.class, "/test/webview", "test", null, -1, -2147483648));
		  }
		}


* Provider，默认实现一些依赖注入的服务，也是通过路由找到类，反射构造实例

		switch (routeMeta.getType()) {
			case PROVIDER:  // if the route is provider, should find its instance
			// Its provider, so it must implement IProvider
				Class<? extends IProvider> providerMeta = (Class<? extends IProvider>) routeMeta.getDestination();
				IProvider instance = Warehouse.providers.get(providerMeta);
				if (null == instance) {
					// There's no instance of this provider
					IProvider provider;
					try {
						provider = providerMeta.getConstructor().newInstance();
						provider.init(mContext);
						Warehouse.providers.put(providerMeta, provider);
						instance = provider;
					}
					catch (Exception e) {
						throw new HandlerException("Init provider failed! " + e.getMessage());
					}
				}
				postcard.setProvider(instance);
				postcard.greenChannel();
				// Provider should skip all of interceptors
				break;
			case FRAGMENT:
				postcard.greenChannel();
			// Fragment needn't interceptors
			default:
				break;
		}


	public class ARouter$$Providers$$module1 implements IProviderGroup {
	  @Override
	  public void loadInto(Map<String, RouteMeta> providers) {
	    providers.put("com.example.module1.IUserService", RouteMeta.build(RouteType.PROVIDER, UserServiceImpl1.class, "/u/1", "u", null, -1, -2147483648));
	    providers.put("com.example.module1.IUserService", RouteMeta.build(RouteType.PROVIDER, UserServiceImpl2.class, "/u/2", "u", null, -1, -2147483648));
	  }
	}


* 拦截,也是继承Iproider。一次性全部加载，每次调用navigation其实都会调用拦截器，看是否调用拦截

	
		public class ARouter$$Interceptors$$app implements IInterceptorGroup {
		  @Override
		  public void loadInto(Map<Integer, Class<? extends IInterceptor>> interceptors) {
		    interceptors.put(7, Test1Interceptor.class);
		    interceptors.put(8, Test1Interceptor2.class);
		  }
		}


#### 缺点

运行时查找dex,扫描后会存储在SharedPreferences中，所以初始化慢


		public synchronized static void init(Context context, ThreadPoolExecutor tpe) throws HandlerException {
		    //load by plugin first
		    loadRouterMap();
		    if (registerByPlugin) {
		        logger.info(TAG, "Load router map by arouter-auto-register plugin.");
		    } else {
		        Set<String> routerMap;
		
		        // It will rebuild router map every times when debuggable.
		        if (ARouter.debuggable() || PackageUtils.isNewVersion(context)) {
		            logger.info(TAG, "Run with debug mode or new install, rebuild router map.");
		            // These class was generated by arouter-compiler.
		            //反射扫描对应包
		            routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);
		            if (!routerMap.isEmpty()) {
		            	//
		                context.getSharedPreferences(AROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).edit().putStringSet(AROUTER_SP_KEY_MAP, routerMap).apply();
		            }
		
		            PackageUtils.updateVersion(context);    // Save new version name when router map update finishes.
		        } else {
		            logger.info(TAG, "Load router map from cache.");
		            routerMap = new HashSet<>(context.getSharedPreferences(AROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).getStringSet(AROUTER_SP_KEY_MAP, new HashSet<String>()));
		        }
		        ....
		    }
		}


#### 优化改进
编译期在loadRouterMap中插入了register代码，通过这种方式即可避免在运行时通过反射扫描className,优化了启动速度
	

	//源码代码，插桩前
	private static void loadRouterMap() {
		//registerByPlugin一直被置为false
	    registerByPlugin = false;
	}
	//插桩后反编译代码
	private static void loadRouterMap() {
	    registerByPlugin = false;
	    register("com.alibaba.android.arouter.routes.ARouter$$Root$$modulejava");
	    register("com.alibaba.android.arouter.routes.ARouter$$Root$$modulekotlin");
	    register("com.alibaba.android.arouter.routes.ARouter$$Root$$arouterapi");
	    register("com.alibaba.android.arouter.routes.ARouter$$Interceptors$$modulejava");
	    register("com.alibaba.android.arouter.routes.ARouter$$Providers$$modulejava");
	    register("com.alibaba.android.arouter.routes.ARouter$$Providers$$modulekotlin");
	    register("com.alibaba.android.arouter.routes.ARouter$$Providers$$arouterapi");
	}

手写arouter的路由demo可参考[https://github.com/chaoyueLin/routerDemo](https://github.com/chaoyueLin/routerDemo)

拦截器或是引申OKHttp的拦截链[https://github.com/chaoyueLin/okhttpDemo](https://github.com/chaoyueLin/okhttpDemo)

#### WMRouter

### resource
getIdentifier()可获取不同module的资源

避免重复
### R.等静态资源的

webview的插件化更新，资源R的分类型编译
### 提升效率工具，脚本
### AOP面向切面编程(Aspect-Oriented Programming)

* [动态代理](https://github.com/chaoyueLin/blog/blob/main/Java/%E4%BB%A3%E7%90%86.md)
* APT(Annotation Processing Tool 的简称)+JavaPoet
* Transform+ASM