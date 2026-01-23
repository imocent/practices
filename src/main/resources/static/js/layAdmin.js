layui.define(["element", "layer"], function (exports) {
    var $ = jQuery = layui.jquery, element = layui.element, layer = layui.layer;
    var modelName = 'layAdmin';
    layui.link((layui.cache.modules[modelName] || '').replaceAll('js', 'css'));

    var tabLayFilter = "lay-tab";
    var navLayFilter = "lay-nav";
    var rememberTab = true;
    var tabList = [];
    var tabsSelector = ".layui-pagetabs .layui-tab-title li[lay-id]";

    var layAdmin = {
        tabAdd: function (o) {
            var id = o.id, url = o.url, title = o.title;
            if (!this.tabExist(id)) {
                // 新增一个Tab项
                element.tabAdd(tabLayFilter, {
                    id: id,
                    title: title,
                    content: "<iframe data-frame-id='" + id + "' class='layui-iframe' src='" + url + "'></iframe>"
                });
                if (rememberTab) {
                    tabList.push({
                        id: id, title: title, url: url
                    });
                    sessionStorage.setItem("tabs", JSON.stringify(tabList));
                }
            }
            element.tabChange(tabLayFilter, id);
        }, tabChange: function (id) {
            element.tabChange(tabLayFilter, id);
        }, tabDelete: function (id) {
            element.tabDelete(tabLayFilter, id);
        }, tabExist: function (id) {
            var isExist = false;
            $.each($(tabsSelector), function () {
                if ($(this).attr("lay-id") === id) {
                    isExist = true;
                    return false;
                }
            });
            return isExist;
        }, flexible: function () {
            if (window.lock) {
                $("#message").show();
                window.lock = false;
                return;
            }
            var elem = $(".layui-layout-admin");
            var flag = elem.hasClass("admin-nav-mini");
            if (flag) {
                $(".layui-nav-item i").css("left", 25)
                elem.removeClass("admin-nav-mini");
            } else {
                $("#message").hide();
                $(".layui-nav-item i").css("left", 20)
                elem.addClass("admin-nav-mini");
            }
            // console.log( elem.hasClass("admin-nav-mini"))
        }, refresh: function () {
            // 硬核刷新法 ~~~
            var iframes = $(".layui-layout-admin .layui-tab .layui-tab-item.layui-show .layui-iframe");
            iframes[0].src = iframes[0].src;
        }, //清空标签缓存
        clear: function () {
            layer.confirm("确认清空标签缓存吗?", {icon: 3, title: "提示"}, function (index) {
                sessionStorage.setItem("tabs", null);
                sessionStorage.setItem("currentTabId", "home");
                layer.close(index);
                layer.msg("清理成功");
            });
        }, //便签
        tag: function () {
            var X = $(this).offset().top;  //获取当前元素x坐标
            var Y = $(this).offset().left; //获取当前元素y坐标
            layer.open({
                title: '📒 便签',
                type: 2,
                area: ['600px', '400px'],
                anim: 2,
                resize: false,
                maxmin: true,
                offset: [X + 50, Y - 350],
                closeBtn: 1,
                shadeClose: true,
                content: '../view/nav/note.html'
            });
        }, //待处理按钮
        todo: function () {
            var X = $(this).offset().top;  //获取当前元素x坐标
            var Y = $(this).offset().left; //获取当前元素y坐标
            layer.open({
                type: 2,
                title: '📝 TODO',
                closeBtn: 1,
                area: ['400px', 'calc(100% - 50px)'],
                offset: [X + 50, Y - 190],
                shadeClose: true,
                skin: 'yourclass',
                content: '../view/nav/todo.html'
            });
        }, //全屏
        screenLayFull: function () {
            if (typeof window.ActiveXObject !== "undefined") {
                //for IE，这里其实就是模拟了按下键盘的F11，使浏览器全屏
                let wscript = new ActiveXObject("WScript.Shell");
                if (wscript != null) {
                    wscript.SendKeys("{F11}");
                }
            } else {
                let el = document;
                let full = el.getElementById("layFull");
                if (full.getAttribute("lay-full") == 'full') {
                    let eld = el.documentElement;
                    let rfs = eld.requestFullScreen || eld.webkitRequestFullScreen || eld.mozRequestFullScreen || eld.msRequestFullScreen;
                    if (rfs) {
                        rfs.call(eld);
                        full.setAttribute("lay-full", "fll");
                    }
                } else {
                    let cfs = el.cancelFullScreen || el.webkitCancelFullScreen || el.mozCancelFullScreen || el.exitFullScreen;
                    if (cfs) {
                        cfs.call(el);
                        full.setAttribute("lay-full", "full");
                    }
                }
            }
        }, //消息通知（左下角通知铃事件）
        notification: function () {
            var X = $(this).offset().top;  //获取当前元素x坐标
            var Y = $(this).offset().left; //获取当前元素y坐标
            layer.open({
                type: 2,
                title: '🔔 消息',
                closeBtn: 1,
                resize: false,
                anim: 2,
                area: ['890px', '710px'],
                offset: [X - 595, Y + 180],
                shadeClose: true,
                content: '../view/nav/messaging.html'
            });
        }, //个人中心
        profile: function () {
            layer.open({
                type: 2,
                title: '🤠 个人中心',
                closeBtn: 1,
                resize: false,
                scrollbar: false,
                area: ['480px', '537px'],
                shadeClose: true,
                content: '/admin/user/detail'
            });
        }, //修改密码
        editPwd: function () {
            layer.open({
                type: 2, title: '🔑 修改密码', closeBtn: 1, // resize:false,
                scrollbar: false, area: ['470px', '342px'], shadeClose: true, content: '/admin/user/cypher'
            });
        }, buildBreadcrumb: function (obj, isLast, html) {
            obj = obj ? obj : $(".layui-side-scroll .layui-this a[lay-url]");
            html = html ? html : "";

            if (typeof isLast === "undefined") {
                isLast = true;
            }

            var currentBreadcurmbHTML,currentNavText = $(obj).first().find("cite").html();
            // falg 为 true 表示最底级的导航.
            if (isLast) {
                currentBreadcurmbHTML = "<a><cite>" + currentNavText + "</cite></a>";
            } else {
                currentBreadcurmbHTML = "<a href='#'>" + currentNavText + "</a><span lay-separator=''>/</span>";
            }
            html = currentBreadcurmbHTML + html;

            var parent = $(obj).parents("dd.layui-nav-itemed");
            if (parent.length === 0) {
                parent = $(obj).parents("li.layui-nav-itemed");
            }
            // 递归查找父导航, 直到没有父导航.
            if (parent.length !== 0) {
                return this.buildBreadcrumb(parent, false, html);
            }
            $("body div.layui-layout-admin div.layui-header ul span.layui-breadcrumb").html(html);
        }, tabPopup: function (config) {
            if ($(".layui-tab[lay-filter='" + config.filter + "']").length === 0) {
                return;
            }
            this.filter = config.filter;
            this.pintabIDs = config.pintabIDs;
            config.navArr = config.navArr || [{eventName: "closeAll", title: "关闭所有"}, {
                eventName: "closeOther", title: "关闭其它"
            }, {eventName: "closeLeft", title: "关闭左侧"}, {eventName: "closeRight", title: "关闭右侧"}];

            var li = "";
            $.each(config.navArr, function (index, conf) {
                if (conf.eventName === "line") {
                    li += "<hr/>";
                } else {
                    li += "<li data-type='" + conf.eventName + "'><i class='layui-icon " + conf.icon + "'></i>" + conf.title + "</li>";
                }
            });
            $(`<ul class='rightmenu'>${li}</ul>`).appendTo("body");

            $(".layui-nav-item").click(function () {
                var elem = $(".layui-layout-admin");
                if (elem.hasClass("admin-nav-mini")) {
                    $(".layui-nav-item i").css("left", 25)
                    elem.removeClass("admin-nav-mini");
                    window.lock = true;
                } else {
                    window.lock = false;
                }
            });

            // tab 页点击右击
            $(".layui-layout.layui-layout-admin .layui-pagetabs").on("contextmenu", "li", function (e) {
                var popupmenu = $(".rightmenu");
                popupmenu.show();
                currentActiveTabID = $(e.target).attr("lay-id");

                var l = ($(document).width() - e.clientX) < popupmenu.width() ? (e.clientX - popupmenu.width()) : e.clientX;
                var t = ($(document).height() - e.clientY) < popupmenu.height() ? (e.clientY - popupmenu.height()) : e.clientY;

                popupmenu.css({left: l, top: t}).show();
                return false;
            });

            // 点击空白处隐藏右键菜单.
            $(document).click(function (e) {
                $(".rightmenu").hide();
            });

            /**
             * 是否允许关闭.
             */
            function isAllowClose(id) {
                return !(layui.layAdmin.pintabIDs && layui.layAdmin.pintabIDs.indexOf(id) !== -1 || id === undefined);
            }

            // 点击右键菜单的功能时.
            $(".rightmenu li").click(function () {
                var tabs = $(".layui-tab[lay-filter='" + config.filter + "'] li");
                switch ($(this).attr("data-type")) {
                    case "closeAll":
                        $.each(tabs, function (i) {
                            var id = $(this).attr("lay-id");
                            if (isAllowClose(id)) {
                                element.tabDelete(config.filter, id);
                            }
                        });
                        break;
                    case "closeOther":
                        $.each(tabs, function (i) {
                            var id = $(this).attr("lay-id");
                            if (isAllowClose(id) && id !== currentActiveTabID) {
                                element.tabDelete(config.filter, id);
                            }
                        });
                        break;
                    case "closeLeft":
                        $.each(tabs, function (i) {
                            var id = $(this).attr("lay-id");
                            if (isAllowClose(id) && id !== currentActiveTabID) {
                                element.tabDelete(config.filter, id);
                            }
                        });
                        break;
                    case "closeRight":
                        var flag = false;
                        $.each(tabs, function (i) {
                            var id = $(this).attr("lay-id");
                            if (id === currentActiveTabID) {
                                flag = true;
                                return true;
                            }

                            if (flag && isAllowClose(id)) {
                                element.tabDelete(config.filter, id);
                            }
                        });
                        break;
                }
                $(".rightmenu").hide();
            });
        },
    };

    // 获取页面上所有的标有 lay-event 的元素, 点击时对应相应的事件.
    $(document).on("click", "*[lay-event]", function (e) {
        let event = $(this).attr("lay-event");
        typeof layAdmin[event] === "function" && layAdmin[event].apply(this);
    });

    element.on("nav(" + navLayFilter + ")", function (elem) {
        // 如果点击的目录还有子目录就不做任何操作.
        if ($(elem).find("span.layui-nav-more").length === 0) {
            var obj = $(this);
            var title = obj.find("cite").html();
            var id = obj.attr("lay-id");
            var url = obj.attr("lay-url");
            if (url != '#') {
                layAdmin.tabAdd({id: id, title: title, url: url});
            }
        }
    });

    // 点击标签卡定位菜单
    element.on("tab(" + tabLayFilter + ")", function (elem) {
        var id = $(this).attr("lay-id");
        var navElem = $(".layui-nav[lay-filter='" + navLayFilter + "']"); //菜单导航元素
        $("div.layui-side-scroll .layui-nav-item").removeClass("layui-nav-itemed").removeClass("layui-this");
        //移除所有选中、获取当前tab选择导航、标注选中样式、展开条目
        navElem.find("li, dd").removeClass("layui-this").find("a[lay-id='" + id + "']").parent().first().addClass("layui-this").parents("li,dd").addClass("layui-nav-itemed");

        layAdmin.buildBreadcrumb();
        if (rememberTab) {
            sessionStorage.setItem("currentTabId", id);
        }
    });
    // 监听 tab 删除事件.
    element.on("tabDelete(" + tabLayFilter + ")", function (elem) {
        tabList.splice(elem.index - 1, 1);
        if (rememberTab) {
            sessionStorage.setItem("tabs", JSON.stringify(tabList));
        }
    });
    // layui 导航互斥效果 (手风琴).
    $("div.layui-side-scroll .layui-nav-item").click(function () {
        var flag = false;
        if ($(this).hasClass("layui-nav-itemed")) {
            flag = true;
        }
        $("div.layui-side-scroll .layui-nav-item").removeClass("layui-nav-itemed").removeClass("layui-this");
        if ($(this).has("dl").length) {//如果有子菜单，显示下拉样式
            if (flag) {
                $(this).addClass("layui-nav-itemed");
            }
        } else {//如果没有子菜单，显示菜单项样式
            $(this).addClass("layui-this");
        }
    });
    // 页面加载完后, 打开存储的标签卡.
    $(document).ready(function () {
        if (rememberTab) {
            var tabs = JSON.parse(sessionStorage.getItem("tabs"));
            var currentTabId = sessionStorage.getItem("currentTabId");
            for (var i = 0; tabs != null && i < tabs.length; i++) {
                layAdmin.tabAdd({id: tabs[i].id, title: tabs[i].title, url: tabs[i].url});
            }
            layAdmin.tabChange(currentTabId);
        }
    });
    // 移动端模式下, 点击遮罩收缩导航.
    $(".site-mobile-shade").click(function () {
        layAdmin.flexible();
    });
    // 点击底部添加文件按钮
    $('.fileAdd').on('click touch', function (e) {
        $(this).toggleClass('open');
        $('.tabbar').toggleClass('open');
    });
    // 搜索模块
    $('.search').each(function () {
        var self = $(this);
        var div = self.children('div');
        var placeholder = div.children('input').attr('placeholder');
        var placeholderArr = placeholder.split(/ +/);
        if (placeholderArr.length) {
            var spans = $('<div />');
            $.each(placeholderArr, function (index, value) {
                spans.append($('<span />').html(value + '&nbsp;'));
            });
            div.append(spans);
        }
        self.click(function () {
            self.addClass('open');
            setTimeout(function () {
                self.find('input').focus();
            }, 750);
        });
        $(document).click(function (e) {
            if (!$(e.target).is(self) && !jQuery.contains(self[0], e.target)) {
                self.removeClass('open');
            }
        });
    });
    exports(modelName, layAdmin);
});