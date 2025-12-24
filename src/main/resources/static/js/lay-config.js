window.rootPath = (function (src) {
    src = document.scripts[document.scripts.length - 1].src;
    return src.substring(0, src.lastIndexOf("/") + 1);
})();

/**
 * 复制到剪切板
 * @param txt_str
 */
function copy_to_clipboard(txt_str) {
    const input = document.createElement('input');
    document.body.appendChild(input);
    input.setAttribute('value', txt_str);
    input.select();
    if (document.execCommand('copy')) {
        document.execCommand('copy');
        layer.msg('复制成功', {icon: 6, time: 1000});
    }
    document.body.removeChild(input);
}

function editView(url, title) {
    openView(url, title, '45%', '65%', false);
}

function openFull(url, title) {
    openView(url, title, '45%', '65%', true);
}

function openView(url, title, widthParam, heightParam, isFull) {
    let viewIndex = layer.open({
        content: url, title: title, area: [widthParam, heightParam], type: 2, fix: false, //不固定
        maxmin: false, shadeClose: true
    });
    if (isFull) {
        layer.full(viewIndex);
    }
}

function advices(data, tier) {
    if (data.code === 0) {
        layer.msg(data.msg, {icon: 6}, function () {
            let index = tier.layer.getFrameIndex(window.name);
            tier.location.replace(tier.location.href)
            tier.layer.close(index);
        });
    } else {
        layer.msg(data.msg, {icon: 5});
    }
}

function toError(answer) {
    if (answer.status === 401) {
        layer.msg("登录超时", {icon: 5, time: 2000});
        parent.window.location.reload(true);//刷新当前页
    } else {
        layer.msg(answer.responseText, {icon: 5, time: 1000});
    }
}

function deleteReq(ids, url) {
    modifyReq(url, {ids: ids}, true)
}

function changeReq(ids, url) {
    modifyReq(url, {ids: ids}, false)
}

function modifyReq(url, dataParam, isReload) {
    layui.jquery.post(url, dataParam, function (data) {
        if (data.code === 0) {
            layer.msg(data.msg, {icon: 6, time: 1000});
            if (isReload) {
                window.location.reload(true);//刷新当前页
            }
        } else {
            layer.msg(data.msg, {icon: 5});
        }
    }, 'json').fail(function (event) {
        toError(event);
    });
}

layui.config({
    base: rootPath
}).extend({
    dtree: 'layDtree', iconPicker: 'layIconPicker', layAdmin: 'layAdmin'
}).use(['table', "layAdmin"], function () {
    // 渲染 tab 右键菜单.
    layui.layAdmin.tabPopup({
        filter: "lay-tab", pintabIDs: ["main", "home"], width: 110,
    });

    layui.jquery("body").on("click", function (event) {
        layui.jquery("div[dtree-id][dtree-select]").removeClass("layui-form-selected");
        layui.jquery("div[dtree-id][dtree-card]").removeClass("dtree-select-show layui-anim layui-anim-upbit");
    });
});