"use strict";
layui.define(['jquery', 'laytpl'], function (e) {
    var mod = 'layChoice', $ = layui.$, tpl = layui.laytpl;
    var pathTemp = layui.cache.modules[mod] || '';
    layui.link(pathTemp.replaceAll('js', 'css'));

    var sys = {
        class: {
            container: 'layui-rc-cascader',
            inputBox: 'cascader-input',
            input: 'cascader-input__inner',
            inputSuffix: 'cascader-input__suffix',
            tags: 'cascader-tags',
            tagBody: 'cascader-tags-body',
            tagItem: 'cascader-tags-item',
            tagNum: 'cascader-tags-num',
            dropdown: 'cascader-dropdown',
            dropdownPanel: 'cascader-dropdown-panel',
            dropdownDl: 'cascader-dropdown-dl',
            dropdownDd: 'cascader-dropdown-dd',
            selectup: 'layui-selectup'
        }, template: {
            main: '<div class="{{d.cls.container}}"><div class="{{d.cls.inputBox}} cascader-input--suffix"><input type="text" readonly placeholder="{{d.opts.placeholder}}" class="{{d.cls.input}} layui-input" /><span class="{{d.cls.inputSuffix}}"><i class="layui-icon layui-icon-triangle-d"></i></span></div><div class="{{d.cls.dropdown}} layui-anim layui-anim-upbit"><div class="{{d.cls.dropdownPanel}}"></div></div>{{# if (d.opts.multiple) { }}<div class="{{d.cls.tags}}"><div class="{{d.cls.tagBody}}"></div></div>{{# } }}</div>',
            dropdownDl: '<div class="{{d.cls.dropdownDl}}">{{# layui.each(d.list, function(i, e){ }}<div class="{{d.cls.dropdownDd}}" data-v="{{e.value}}"><span>{{e.label}}</span>{{# if (e.hasChildren) { }}<i class="layui-icon layui-icon-right"></i>{{# } }}<i class="layui-icon layui-icon-ok"></i></div>{{# }); }}</div>',
            tags: '{{# layui.each(d.list, function (i, e) { }}<div class="{{d.cls.tagItem}}" data-v="{{e.value}}"><span>{{ e.label }}</span><i class="layui-icon layui-icon-close-fill"></i></div>{{# }); }}',
            tagsCollapse: '<div class="{{d.cls.tagItem}}" data-v="{{d.list[0].value}}"><span>{{d.list[0].label}}</span><i class="layui-icon layui-icon-close-fill"></i></div><div class="{{d.cls.tagItem}} {{d.cls.tagNum}}">+{{d.list.length}}</div>'
        }
    };

    var selected = [];

    var Cascader = function (opts) {
        var _s = this;
        _s.config = $.extend({}, _s.config, opts);
        _s.render();
    }

    Cascader.prototype.config = {
        elem: '',          // 存储ID的输入框
        labelElem: null,   // 可选,存储名的输入框
        url: '',
        method: 'GET',
        request: {},
        response: {statusCode: 0, data: 'data'},
        headers: {},
        options: [],
        multiple: false,    // 选择模式: false-单选,true-多选
        clearable: false,
        collapseTags: true,
        filterable: false,
        showAllLevels: true,
        placeholder: '请选择',
        separator: '/',
        valueSeparator: ',',
        groupSeparator: '|',
        props: {label: 'title', value: 'id', children: 'child'},
        debounce: 300
    };

    // ---------------- 渲染 ----------------
    Cascader.prototype.render = function () {
        var _s = this, _e = _s.config.elem;
        $(_e).parent().find(`.${sys.class.container}`).remove();
        $(_e).hide().after(tpl(sys.template.main).render({cls: sys.class, opts: _s.config}));

        _s.eventRegister();

        if (_s.config.url) {
            _s.loadRemoteData();
        } else {
            _s.renderData([]);
            _s.initByValue();
        }
    };

    // ---------------- 远程加载 ----------------
    Cascader.prototype.loadRemoteData = function () {
        var _s = this, cfg = _s.config;
        $.ajax({
            url: cfg.url,
            type: cfg.method || 'GET',
            data: cfg.request || {},
            headers: cfg.headers || {},
            dataType: 'json',
            success: function (res) {
                if (res.code !== cfg.response.statusCode) {
                    console.error('cascader request error:', res);
                    return;
                }
                var data = cfg.response.data ? res[cfg.response.data] : res;
                if (!Array.isArray(data)) {
                    console.error('cascader data must be array');
                    return;
                }
                _s.config.options = data;
                _s.renderData([]);
                _s.initByValue();
            },
            error: function (xhr) {
                console.error('cascader ajax error', xhr);
            }
        });
    };

    // ---------------- 事件 ----------------
    Cascader.prototype.eventRegister = function () {
        var _s = this, _e = _s.config.elem, _cls = sys.class, $c = $(_e).next();

        $c.find(`.${_cls.inputBox}`).on('click', _s.onShow.bind(_s));
        $c.find(`.${_cls.tags}`).on('click', _s.onShow.bind(_s));

        $c.find(`.${_cls.tags}`).on('click', `.${_cls.tagItem} > i`, function (e) {
            e.stopPropagation();
            _s.onSelect.bind(_s)($(this).closest(`.${_cls.tagItem}`).data('v'));
        });

        $c.on('click', `.${_cls.dropdownDd}`, function (e) {
            e.stopPropagation();
            _s.onSelect.bind(_s)($(this).data('v'));
        });

        $(document).on('click', function (e) {
            var _target = e.target;
            if ($c.find(_target).length === 0) {
                _s.onClose(e);
            }
        });
    };

    // ---------------- 渲染选项 ----------------
    Cascader.prototype.renderData = function (treePath) {
        var _s = this, _e = _s.config.elem, _cls = sys.class, $c = $(_e).next(), $dp = $c.find(`.${_cls.dropdownPanel}`),
            _options = _s.config.options;

        if (treePath.length > 0) {
            _options = _s.getChildren(treePath);
        }

        $dp.find(`.${_cls.dropdownDl}`).each(function (i, e) {
            if (i >= treePath.length) $(e).remove();
        });

        if (_options.length === 0) return;

        var _$ddList = $(tpl(sys.template.dropdownDl).render({
            list: _options.map(function (e) {
                return {
                    label: e[_s.config.props.label],
                    value: treePath.concat([e[_s.config.props.value]]).join(_s.config.valueSeparator),
                    hasChildren: e[_s.config.props.children] && e[_s.config.props.children].length > 0
                };
            }), cls: sys.class
        }));
        _$ddList.appendTo($dp);
        _s.highlight();
    };

    // ---------------- 选择 ----------------
    Cascader.prototype.onSelect = function (v) {
        var _s = this, _e = _s.config.elem, _treePath = (`${v}`).split(_s.config.valueSeparator);
        var _item = _s.getItemByPath(_treePath);
        if (_s.getChildren(_treePath).length > 0) {
            selected = _treePath;
            _s.renderData(_treePath);
            _s.highlight();
            return;
        }
        if (_s.config.multiple) {
            var _v = _s.getSelectedValue(), value = _s.convertValue(_item);
            var _i = _v.indexOf(value);
            if (_i >= 0) _v.splice(_i, 1); else _v = _v.concat(value);

            $(_e).val(_v.join(_s.config.groupSeparator));
        } else {
            var label = _s.convertInputText(_item);
            var value = _s.convertValue(_item);
            // 写入 valueElem
            $(_e).val(value);
            // 写入 labelElem
            if (_s.config.labelElem) $(_s.config.labelElem).val(label);
            _s.onClose();
        }
        _s.showLabel();
        _s.highlight();
    };

    // ---------------- 初始化回显 ----------------
    Cascader.prototype.initByValue = function () {
        var _s = this, values = _s.getSelectedValue();
        if (!values.length) return;

        var path = values[0].split(_s.config.valueSeparator);
        selected = path;

        _s.renderData(path.slice(0, path.length - 1));
        _s.highlight();
        _s.showLabel();
    };
    // ---------------- 显示文字 ----------------
    Cascader.prototype.showLabel = function () {
        var _s = this, _e = _s.config.elem, _cls = sys.class, $c = $(_e).next();
        var $tags = $c.find(`.${_cls.tags}`);
        var _selectedOptions = _s.getSelectOptions();

        if (_s.config.multiple) {
            var $input = $c.find(`.${_cls.input}`), $tagBody = $tags.find(`.${_cls.tagBody}`);
            var _labels = _selectedOptions.map(function (e) {
                return {label: _s.convertInputText(e), value: _s.convertValue(e)};
            });
            if (_labels.length === 0) {
                $input.attr('placeholder', _s.config.placeholder);
                $input.height('');
                $tags.hide();
                return;
            }
            tpl(_s.config.collapseTags ? sys.template.tagsCollapse : sys.template.tags).render({
                cls: sys.class, list: _labels
            }, function (html) {
                $tagBody.html(html);
                setTimeout(function () {
                    $input.attr('placeholder', '');
                    $input.height($tags.height() + 2);
                    $tags.show();
                }, 300);
            });
        } else {
            $c.find(`.${_cls.input}`).val(_s.convertInputText(_selectedOptions ? _selectedOptions[0] : null));
        }
    };
    // ---------------- 辅助方法 ----------------
    Cascader.prototype.getChildren = function (path) {
        var _s = this;
        if (!Array.isArray(path)) path = path.split(_s.config.valueSeparator);

        return path.reduce(function (res, e) {
            var _selected = res.filter(function (_e) { return _e[_s.config.props.value].toString() === e.toString(); });
            _selected = _selected.length > 0 ? _selected[0] : {};
            return _selected.hasOwnProperty(_s.config.props.children) ? _selected[_s.config.props.children] : [];
        }, _s.config.options);
    };

    Cascader.prototype.getItemByPath = function (path) {
        var _s = this, _options = _s.config.options;
        if (!Array.isArray(path)) path = path.split(_s.config.valueSeparator);

        return path.reduce(function (res, e) {
            var restruct = _options.filter(function (_e) { return _e[_s.config.props.value].toString() === e.toString(); });
            if (restruct.length > 0) {
                res.push(restruct[0]);
                _options = restruct[0][_s.config.props.children] || [];
            }
            return res;
        }, []);
    };

    Cascader.prototype.getSelectOptions = function () {
        var _s = this, _v = _s.getSelectedValue();
        return _v.map(function (el) {
            var _options = _s.config.options;
            return el.split(_s.config.valueSeparator).reduce(function (res, e) {
                var restruct = _options.filter(function (_e) { return _e[_s.config.props.value].toString() === e.toString(); });
                if (restruct.length > 0) {
                    res.push(restruct[0]);
                    _options = restruct[0][_s.config.props.children] || [];
                }
                return res;
            }, []);
        });
    };

    Cascader.prototype.getSelectedValue = function () {
        var _s = this, _e = _s.config.elem;
        var value = $(_e).val() === "" ? [] : $(_e).val().split(_s.config.groupSeparator);
        return Array.isArray(value) ? value : [value];
    };

    Cascader.prototype.convertInputText = function (v) {
        if (!v) return '';
        var _s = this;
        return _s.config.showAllLevels ? v.map(function (e) { return e[_s.config.props.label]; }).join(` ${_s.config.separator} `) : v[v.length - 1][_s.config.props.label];
    };

    Cascader.prototype.convertValue = function (v) {
        var _s = this;
        if (!Array.isArray(v)) v = [v];
        return v.map(function (e) { return e[_s.config.props.value]; }).join(_s.config.valueSeparator);
    };

    Cascader.prototype.highlight = function (callback) {
        var _s = this, _e = _s.config.elem, _cls = sys.class, $c = $(_e).next(), $dp = $c.find(`.${_cls.dropdownPanel}`);
        var _v = _s.getSelectedValue();

        var _marginObject = function (arr, obj) {
            var e = arr.shift();
            if (!obj.hasOwnProperty(e)) obj[e] = {};
            if (arr.length > 0) obj[e] = _marginObject(arr, obj[e]);
            return obj;
        };

        _v = _v.concat(selected.join(_s.config.valueSeparator)).reduce(function (res, e) {
            return _marginObject(e.split(_s.config.valueSeparator), res);
        }, {});

        $dp.find(`.${_cls.dropdownDd}`).removeClass('selected in-active');
        $dp.find(`.${_cls.dropdownDl}`).each(function (i, e) {
            if (!_v) return;
            var _keys = Object.keys(_v);
            if (_keys.length > 0) {
                _keys.forEach(function (_e) {
                    var _key = selected.slice(0, i).concat(_e).join(_s.config.valueSeparator);
                    $(e).find(`.${_cls.dropdownDd}[data-v="${_key}"]`).addClass(_s.getChildren(_key).length > 0 ? (_s.config.multiple ? 'in-active' : '') : 'selected');
                });
                _v = _v[selected[i]];
            }
        });

        if (callback) callback.call(this);
    };

    Cascader.prototype.onShow = function () {
        var _s = this, _e = _s.config.elem, $c = $(_e).next(), _cls = sys.class, $input = $c.find(`.${_cls.input}`);
        if ($c.find(`.${_cls.inputBox}`).hasClass('focus')) return _s.onClose();
        if (document.body.offsetHeight - ($input.offset().top + $input.height()) < 300 && $input.offset().top > 300) $c.addClass(_cls.selectup);
        $c.find(`.${_cls.inputBox}`).addClass('focus');
    };

    Cascader.prototype.onClose = function () {
        var _e = this.config.elem, $c = $(_e).next(), _cls = sys.class;
        $c.removeClass(_cls.selectup);
        $c.find(`.${_cls.inputBox}`).removeClass('focus');
    };

    // ---------------- 导出 ----------------
    e(mod, {
        render(opts) {
            if (!opts.elem) return console.error(mod, 'elem is undefined');
            if (typeof opts.elem === 'string') {
                opts.elem = document.querySelector(opts.elem);
                if (!opts.elem) return console.error(mod, 'elem not found: ' + opts.elem);
            } else if (!(opts.elem instanceof HTMLElement)) return console.error(mod, 'elem is not HTMLElement');
            return new Cascader(opts);
        }
    });
});
