---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by White.
--- DateTime: 2023/1/9 13:49
---

--      lua学习第六章 协同线程和协同函数

--  一、定义协同线程
--      ①可以在运行时暂停执行，然后转去执行其它线程，然后还可返回再继续执行没有执行完毕的内容。
print("--1.协同线程")

-- 1.创建一个协同线程实例
crt = coroutine.create(function(a, b)
    print(a, b, a + b)
    --running()：返回正在运行的协同线程实例
    tr1 = coroutine.running()
    print(tr1)
    --查看协同线程的类型
    print(type(tr1))
    --查看协同线程的状态
    print(coroutine.status(tr1))
    --将当前协同线程实例挂起,同时携带两个返回值
    coroutine.yield(a * b, a / b)
    print("重新返回了协同线程")

    --返回两个值
    return a + b, a - b
end)


----  2.启动协同线程实例
--coroutine.resume(crt,3,5)
----查看crt的状态
--print("main-"..coroutine.status(crt))
----继续执行crt线程
--coroutine.resume(crt,3,5)
----查看crt的状态
--print("main-"..coroutine.status(crt))


-- 3.启动协同线程实例2
success, result1, result2 = coroutine.resume(crt, 12, 3)
print(success, result1, result2) --success代表线程启动情况
--查看crt的状态
print("main-" .. coroutine.status(crt))

--将挂起的crt线程启动
success, result1, result2 = coroutine.resume(crt)
print(success, result1, result2)


--------------------------------------------------------------------
--  二、协同函数
--      ① 协同线程可以单独创建执行，也可以通过协同函数的调用启动执行。
print("--2.协同函数")

cf = coroutine.wrap(function(a, b)
    print(a, b)

    --获取当前协同函数创建的协同线程
    tr = coroutine.running()
    --查看协同线程的类型
    print(type(tr))
    --查看协同线程的状态
    print(coroutine.status(tr))
    --将当前协同线程实例挂起,同时携带两个返回值
    coroutine.yield(a + 1, b + 1)

    print("重新返回了协同线程")

    --返回两个值
    return a + b, a * b
end)

re1, re2 = cf(5, 4)
print(re1,re2)
--查看cf的类型
print("cf的类型",type(tr))
--重启挂起的cf线程
re1, re2 = cf(5, 4)
print(re1,re2)