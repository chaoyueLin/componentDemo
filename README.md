# 语音助手组件化演化
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
#### Lifecycle
### 组件化通信
### 路由
#### [Arouter](https://github.com/alibaba/ARouter)
* 路由原理![](./img/arouter.png)
* 服务调用Iproider，SPI，全称Service Provider Interfaces，服务提供接口。是Java提供的一套供第三方实现或扩展使用的技术体系。解耦了服务提供与服务使用
* 拦截,也是继承Iproider。一次性全部加载，每次调用navigation其实都会调用拦截器，看是否调用拦截

手写arouter的路由demo可参考[https://github.com/chaoyueLin/routerDemo](https://github.com/chaoyueLin/routerDemo)

拦截器或是引申OKHttp的拦截链[https://github.com/chaoyueLin/okhttpDemo](https://github.com/chaoyueLin/okhttpDemo)

#### WMRouter
### 资源resource的避免重复
### R.等静态资源的
### 提升效率工具，脚本