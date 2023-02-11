---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by White.
--- DateTime: 2023/1/9 11:16
---

--    lua学习——第四章 元表与元方法

-- 二、元表单独定义

-- 1.元表单独定义
meta = {
    __add = function(table, num)
        --遍历table中所有元素
        for i, v in pairs(table) do
            --如果类型为number
            if type(v) == "number" then
                table[i] = v + num
                --如果类型为string
            elseif type(v) == "string" then
                table[i] = v .. num
            end
        end
        --将操作后的表进行返回
        return table
    end, --注意使用","分割重写的方法

    --重写tostring方法
    __tostring = function(table)
        str = ""
        for i, v in pairs(empsum) do
            str = str .. " " .. v
        end
        return str
    end,

    --重写__call元方法
    __call = function(table, num, str)
        --遍历table中所有元素
        for i, v in pairs(table) do
            if type(v) == "number" then
                table[i] = v + num
            elseif type(v) == "string" then
                table[i] = v .. str
            end
        end
        return table
    end
}