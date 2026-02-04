window.rootPath = (function (src) {
    src = document.scripts[document.scripts.length - 1].src;
    return src.substring(0, src.lastIndexOf("/") + 1);
})();

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