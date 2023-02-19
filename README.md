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