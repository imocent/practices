/**
 * 复制到剪切板
 * @param {string} txt_str - 要复制的文本
 */
function copy_to_clipboard(txt_str) {
    if (!txt_str) return;

    const input = document.createElement('input');
    input.setAttribute('value', txt_str);
    input.setAttribute('readonly', 'readonly');
    input.style.position = 'absolute';
    input.style.left = '-9999px';

    document.body.appendChild(input);
    input.select();

    try {
        const success = document.execCommand('copy');
        if (success) {
            layer.msg('复制成功', {icon: 6, time: 1000});
        } else {
            layer.msg('复制失败', {icon: 5, time: 1000});
        }
    } catch (err) {
        layer.msg('复制失败', {icon: 5, time: 1000});
        console.error('复制错误:', err);
    } finally {
        document.body.removeChild(input);
    }
}

/**
 * 编辑视图
 * @param {string} url - 视图URL
 * @param {string} title - 视图标题
 */
function editView(url, title) {
    openView(2, title, '45%', '70%', false, url);
}

/**
 * 打开全屏视图
 * @param {string} url - 视图URL
 * @param {string} title - 视图标题
 */
function openFull(url, title) {
    openView(2, title, '45%', '65%', true, url);
}

/**
 * 打开HTML视图
 * @param {string} title - 视图标题
 * @param {string} html - HTML内容
 */
function openHtml(title, html) {
    const in_notes = `<div class="layui-card layui-card-body">${html || 暂无内容}</div>`;
    openView(1, title, '45%', '65%', false, in_notes);
}

/**
 * 打开视图
 * @param {number} opType - 操作类型
 * @param {string} title - 视图标题
 * @param {string} widthParam - 宽度
 * @param {string} heightParam - 高度
 * @param {boolean} isFull - 是否全屏
 * @param {string} in_content - 内容
 */
function openView(opType, title, widthParam, heightParam, isFull, in_content) {
    const viewIndex = layer.open({
        title: title, area: [widthParam, heightParam], fix: false, maxmin: false, shadeClose: true, type: opType, content: in_content,
    });

    if (isFull && viewIndex) {
        layer.full(viewIndex);
    }
}

/**
 * 全局变量
 */
var GLOBAL = {
    chooses: [] //用于弹出层多选
};

/**
 * 打开页面
 * @param {Object} params - 页面参数
 */
function openPage(params) {
    // 参数初始化
    params = params || {};
    if (!params.width) {
        params.width = '45%';
    }
    if (!params.height) {
        params.height = '65%';
    }
    if (typeof params.btn === "undefined") {
        params.btn = ['确认', '取消'];
    }
    //0-信息框(默认),1-页面层,2-iframe层,3-加载层,4-tips层
    return layer.open({
        type: 2,
        title: params.title,
        content: params.content,
        area: [params.width, params.height],
        fix: false,
        maxmin: false,
        shadeClose: true,
        btn: params.btn,
        yes: function (index, layero) {
            if (typeof params.yes === 'function') {
                params.yes(index, layero);
            } else if (params.saveUrl) {
                layui.form.on('submit(*)', function (data) {
                    let field = data.field || {};
                    if (typeof params.beforeSubmit === 'function') {
                        const result = params.beforeSubmit(field);
                        if (result === false) {
                            return false;
                        }
                        if (result !== true && result !== undefined) {
                            field = result;
                        }
                    }
                    modifyReq(params.saveUrl, field);
                    return false;
                });

                if (layui.form.validate) {
                    layui.form.validate();
                }
                layer.close(index);
            }
        },
        btn2: function (index, layero) {
            if (typeof params.btn2 === 'function') {
                params.btn2(index, layero);
            } else {
                layer.close(index);
            }
        },
        cancel: function (index, layero) {
            if (typeof params.cancel === 'function') {
                params.cancel(index, layero);
            } else {
                layer.close(index);
            }
        },
        success: function (layero, index) {
            if (typeof params.success === 'function') {
                params.success(index, layero);
            }
            if (layui.form) {
                layui.form.render();
            }
        },
        end: function (layero, index) {
            if (typeof params.end === 'function') {
                params.end(index, layero);
            }
        }
    });
}

/**
 * 处理操作结果
 * @param {Object} answer - 返回数据
 * @param {Object} tier - 层级对象
 */
function advices(answer, tier) {
    if (!answer) return;

    if (answer.code === 0) {
        layer.msg(answer.msg || '操作成功', {icon: 6}, function () {
            if (tier) {
                const index = tier.layer ? tier.layer.getFrameIndex(window.name) : null;
                if (index) {
                    tier.location.replace(tier.location.href);
                    tier.layer.close(index);
                }
            }
        });
    } else {
        layer.msg(answer.msg || '操作失败', {icon: 5}, function () {
            if (answer.redirect) {
                if (answer.url) {
                    window.location.href = answer.url;
                } else {
                    parent.window.location.reload();
                }
            }
        });
    }
}

/**
 * 处理错误
 * @param {Object} answer - 错误对象
 */
function toError(answer) {
    if (!answer) return;

    if (answer.status === 401) {
        layer.msg("登录超时", {icon: 5, time: 2000}, function () {
            if (answer.redirect) {
                if (answer.url) {
                    window.location.href = answer.url;
                } else {
                    parent.window.location.reload();
                }
            }
        });
    } else {
        const msg = answer.responseText || '请求失败';
        layer.msg(msg, {icon: 5, time: 1000});
    }
}

/**
 * 删除请求
 * @param {string|Array} ids - ID或ID数组
 * @param {string} url - 请求URL
 */
function deleteReq(ids, url) {
    modifyReq(url, {ids: ids}, true);
}

/**
 * 修改请求
 * @param {string|Array} ids - ID或ID数组
 * @param {string} url - 请求URL
 */
function changeReq(ids, url) {
    modifyReq(url, {ids: ids}, false);
}

/**
 * 发送修改请求
 * @param {string} url - 请求URL
 * @param {Object} dataParam - 请求参数
 * @param {boolean} isReload - 是否刷新页面
 */
function modifyReq(url, dataParam, isReload) {
    if (!url || !dataParam) return;

    layui.jquery.post(url, dataParam, function (data) {
        if (data && data.code === 0) {
            layer.msg(data.msg || '操作成功', {icon: 6, time: 1000});
            if (isReload) {
                window.location.reload();
            }
        } else {
            layer.msg((data && data.msg) || '操作失败', {icon: 5});
        }
    }, 'json').fail(function (event) {
        toError(event);
    });
}

/**
 * 从数组中移除元素
 * @param {Array} arr - 原数组
 * @param {*} v - 要移除的值
 * @returns {Array} 新数组
 */
function removeFromArray(arr, v) {
    if (!Array.isArray(arr)) return arr;

    const index = indexOfArray(arr, v);
    if (index > -1) {
        arr.splice(index, 1);
    }
    return arr;
}

/**
 * 向数组添加元素（不重复）
 * @param {Array} arr - 原数组
 * @param {*} v - 要添加的值
 * @returns {Array} 新数组
 */
function pushArray(arr, v) {
    if (!Array.isArray(arr)) return arr;

    if (indexOfArray(arr, v) === -1) {
        arr.push(v);
    }
    return arr;
}

/**
 * 查找元素在数组中的索引
 * @param {Array} arr - 数组
 * @param {*} v - 要查找的值
 * @returns {number} 索引，-1表示不存在
 */
function indexOfArray(arr, v) {
    if (!Array.isArray(arr)) {
        return -1;
    }

    for (let i = 0; i < arr.length; i++) {
        if (arr[i] === v) {
            return i;
        }
    }
    return -1;
}