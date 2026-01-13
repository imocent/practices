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
    openView(2, title, '45%', '65%', false, url);
}

function openFull(url, title) {
    openView(2, title, '45%', '65%', true, url);
}

function openHtml(title, html) {
    openView(1, title, '45%', '65%', false, `<div class="layui-card layui-card-body">${html || 暂无内容}</div>`);
}

function openView(opType, title, widthParam, heightParam, isFull, in_content) {
    let viewIndex = layer.open({
        title: title, area: [widthParam, heightParam], fix: false, maxmin: false, shadeClose: true, type: opType, content: in_content,
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
    layAdmin: 'layAdmin', layChoice: 'layChoice', dtree: 'layDtree', layEditor: 'layEditor', iconPicker: 'layIconPicker'
}).use(['jquery', "layAdmin"], function ($, layAdmin) {
    // 渲染 tab 右键菜单.
    layAdmin.tabPopup({
        filter: "lay-tab", pintabIDs: ["main", "home"], width: 110,
    });

    $("body").on("click", function (event) {
        $("div[dtree-id][dtree-select]").removeClass("layui-form-selected");
        $("div[dtree-id][dtree-card]").removeClass("dtree-select-show layui-anim layui-anim-upbit");
    });
});