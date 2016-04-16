
Ext.onReady(function(){

    var currentRow = null;

    var start = 0;
    var pageSize = 15;

    var resourceRecord = Ext.data.Record.create(
        [
            {name: 'id',mapping:'id'},
            {name:'userName',mapping:'userName'},
            {name: 'ip',mapping:'ip'},
            {name: 'mac',mapping:'mac'},
            {name: 'createTime',mapping:'createTime'},
            {name: 'isconnect',mapping:'isconnect'},
            {name: 'connectTime',mapping:'connectTime'}
        ]);

    var store = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'ClientUserAction_select.action',method:'POST'}),

        reader: new Ext.data.JsonReader({
            totalProperty:"total",
            root: 'rows'
        }, resourceRecord)
    });
    store.load({
        params:{
            start:start,limit:pageSize
        }
    });


    var state = function(value) {
        if(value == null){
            return "<img src='img/icon/bjgl.png' alt='未知'/>";
        }else  if(value==true){
            return "<img src='img/icon/ok.png' alt='认证成功'/> ";
        }else if (value== false){
            return "<img src='img/icon/off.gif' alt='关闭或网络异常'/> ";
        }else{
            return "<img src='img/icon/bjgl.png' alt='未知'/>";
        }
    }
    var rowNumber = new Ext.grid.RowNumberer();         //自动 编号
    var resourceCm = new Ext.grid.ColumnModel([
        rowNumber,
//        {header: "编号", width: 50,align:'center', dataIndex: 'id',sortable:true,menuDisabled:true},
        {header: "认证用户名", width: 50,align:'center', dataIndex: 'userName',sortable:true,menuDisabled:true},
        {header: "客户端IP", width: 100, dataIndex: 'ip',align:'center',sortable:true,menuDisabled:true},
        {header: "客户端MAC", width: 100, dataIndex: 'mac',align:'center',sortable:true,menuDisabled:true},
        {header: "当前状态", width: 100, dataIndex: 'isconnect',align:'center',sortable:true,menuDisabled:true,renderer:state},
        {header: "最近认证时间", width: 100, dataIndex: 'connectTime',align:'center',sortable:true,menuDisabled:true},
        {
            header:"操作",
            dataIndex: '0',
            align:"center",
            sortable : false,
            width:100,
            renderer:function(v){
                return "<a href='javascript:;' onclick='changePwd()'>修改密码</a>  <a href='javascript:;' onclick='setPermission()'>分配权限</a>";
            }
        }
    ]);

    // create the grid
    var grid = new Ext.grid.GridPanel({
        id:'clientUser',
        store: store,
        cm: resourceCm,
        title:'认证账号管理',
        viewConfig: {
            forceFit:true
        },
        columnLines: true,
        autoScroll:true,
        loadMask:{msg:'正在加载数据，请稍后...'},
        border:false,
        collapsible:false,
        stripeRows:true,
        autoExpandColumn:'Position',
        enableHdMenu:true,
        enableColumnHide:true,
//        bodyStyle:'width:100%',
        selModel:new Ext.grid.RowSelectionModel({singleSelect:false}),
        height:300,
        frame:true,
        iconCls:'icon-grid',

        // inline toolbars
        tbar:[{
            text: '新增认证账号',
            iconCls:'add',
            handler: function(){addresource(grid,store);}
        },'-',{
            text: '编辑认证账号',
            iconCls:'upgrade',
            handler: function(){editresource(grid,store);}
        },'-',{
            text: '删除认证账号',
            iconCls:'remove',
            handler: function(){delresource(grid,store);}
        },'-','认证用户名:',{
            id : 'userName',
            xtype : 'textfield',
            name : 'userName',
            emptyText : '点击输入设备名称'   ,
            width:120
        },{
            xtype: 'tbseparator'
        },
            {
                // xtype: 'button',
                text : '查询',
                listeners : {
                    "click" : function() {
                        store.load({
                            params : {
                                userName:Ext.getCmp("userName").getValue()
                            }
                        });
                    }
                }
            }]
//        bbar:pagebar
    });
    new Ext.Viewport({
        border:false,
        renderTo:Ext.getBody(),
        layout:'fit',
        items:[grid],
        autoScroll:true
        /*layout:'anchor',
        items:[
            {autoScroll:true,items:[resourceGrid],height:.3},
            {autoScroll:true,items:[resourceGrid_Run],height:.3},
            {autoScroll:true,items:[resourceGrid_Close],height:.4}
        ]*/
    });

    function addresource(grid,store){
        var formPanel = new Ext.form.FormPanel({
            frame:true,
            autoScroll:true,
            labelWidth : 120, // label settings here cascade unless overridden
            border:false,
            bodyStyle : 'padding:5px 5px 0',
            width : 500,
            waitMsgTarget : true,
            defaults : {
                width : 230
            },
            defaultType : 'textfield', //
            items:[
                {
                    fieldLabel:"认证用户名",
                    name:'clientUser.userName',
                    regex:/^.{2,30}$/,
                    regexText:'请输入任意2--30个字符',
                    emptyText:'请输入任意2--30个字符'
                } ,{
                    id:'password.info',
                    fieldLabel:"认证密码",
                    name:'clientUser.userPwd',
                    inputType: 'password',
                    regex:/^.{8,30}$/,
                    regexText:'密码规则:8~30位!',
                    emptyText:'请输入密码!'
                },{
                    id:'password2.info',
                    fieldLabel:"确认密码",
                    name:'password2',
                    inputType: 'password',
                    regex:/^.{8,30}$/,
                    regexText:'密码规则:8~30位!',
                    emptyText:'请输入密码!',
                    listeners:{
                        blur : function(){
                            var password = Ext.getCmp('password.info').getValue();
                            if(password.length>0){
                                var password2 = this.getValue();
                                if(password != password2&&password2.length>0){
                                    Ext.MessageBox.show({
                                        title:'信息',
                                        width:250,
                                        msg:'<font color="red">"确认密码"和"密码"不一致!</font>',
                                        animEl:'password2.info',
                                        buttons:{'ok':'确定'},
                                        icon:Ext.MessageBox.INFO,
                                        closable:false,
                                        fn:function(e){
                                            if(e=='ok'){
                                                Ext.getCmp('password2.info').setValue('');
                                            }
                                        }
                                    });
                                }
                            } else {
                                Ext.MessageBox.show({
                                    title:'信息',
                                    width:270,
                                    msg:'<font color="red">请先输入"密码",再输入"确认密码"!</font>',
                                    animEl:'password2.info',
                                    buttons:{'ok':'确定'},
                                    icon:Ext.MessageBox.INFO,
                                    closable:false,
                                    fn:function(e){
                                        if(e=='ok'){
                                            Ext.getCmp('password2.info').setValue('');
                                        }
                                    }
                                });
                            }
                        }
                    }
                } ,{
                    id:'ip.info',
                    fieldLabel:"客户端IP",
                    name:'clientUser.ip',
                    regex:/^(((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9]))$/,
                    regexText:'这个不是Ip',
                    emptyText:'请输入Ip'
                },{
                    id:'mac.info',
                    fieldLabel:'客户端MAC',
                    name:'clientUser.mac',
                    regex:/([0-9a-fA-F]{2})(([/\s:-][0-9a-fA-F]{2}){5})/,
                    regexText:'这个不是mac地址',
                    emptyText:'请输入网卡mac'
            }]
        });
        var win = new Ext.Window({
            title:'新增认证账号',
            width:500,
            layout:'fit',
            height:250,
            modal:true,
            items:formPanel,
            bbar:[
                '->',{
                    id:'insert_win.info',
                    text:'保存',
                    handler:function(){
                        if (formPanel.form.isValid()) {
                            formPanel.getForm().submit({
                                url :'ClientUserAction_insert.action',
                                method :'POST',
                                waitTitle :'系统提示',
                                waitMsg :'正在保存,请稍后...',
                                success : function(form,action) {
                                    var msg = action.result.msg;
                                    Ext.MessageBox.show({
                                        title:'信息',
                                        width:250,
                                        msg:msg,
                                        animEl:'insert_win.info',
                                        buttons:{'ok':'确定','no':'取消'},
                                        icon:Ext.MessageBox.INFO,
                                        closable:false,
                                        fn:function(e){
                                            if(e=='ok'){
                                                grid.render();
                                                store.reload();
                                                win.close();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Ext.MessageBox.show({
                                title:'信息',
                                width:200,
                                msg:'请填写完成再提交!',
                                animEl:'insert_win.info',
                                buttons:{'ok':'确定'},
                                icon:Ext.MessageBox.ERROR,
                                closable:false
                            });
                        }
                    }
                }
            ]
        });
        win.show();
    }

    function editresource(grid,store){
        var selModel = grid.getSelectionModel();
        if(selModel.hasSelection()){
            var selections = selModel.getSelections();
            Ext.each(selections,function(item){
                var formPanel = new Ext.form.FormPanel({
                    frame:true,
                    autoScroll:true,
                    labelWidth : 120, // label settings here cascade unless overridden
                    border:false,
                    bodyStyle : 'padding:5px 5px 0',
                    width : 500,
                    waitMsgTarget : true,
                    defaults : {
                        width : 230,
                        allowBlank:false
                    },
                    defaultType : 'textfield', //
                    items:[{
                        id:'update.id',
                        name:'clientUser.id',
                        xtype:'hidden',
                        value:item.data.id
                    },
                        {
                            fieldLabel:"认证用户名",
                            name:'clientUser.userName',
                            regex:/^.{2,30}$/,
                            regexText:'请输入任意2--30个字符',
                            emptyText:'请输入任意2--30个字符',
                            value:item.data.userName
                        }/* ,{
                            id:'password.info',
                            fieldLabel:"认证密码",
                            name:'clientUser.userPwd',
                            inputType: 'password',
                            regex:/^.{8,30}$/,
                            regexText:'密码规则:8~30位!',
                            emptyText:'请输入密码!',
                            listeners:{
                                blur:function(){
                                    var password = this.getValue();
                                    if(password.length>0){
                                        var myMask = new Ext.LoadMask(Ext.getBody(),{
                                            msg:'正在校验,请稍后...',
                                            removeMask:true
                                        });
                                        myMask.show();
                                        Ext.Ajax.request({
                                            url : 'ClientUserAction_checkPwd.action',
                                            params :{password:password,id:Ext.getCmp('update.id').getValue()},
                                            method:'POST',
                                            success : function(r,o) {
                                                var respText = Ext.util.JSON.decode(r.responseText);
                                                var msg = respText.msg;
                                                myMask.hide();
                                                if(msg != 'true'){
                                                    Ext.MessageBox.show({
                                                        title:'信息',
                                                        width:250,
                                                        msg:msg,
                                                        buttons:{'ok':'确定'},
                                                        icon:Ext.MessageBox.INFO,
                                                        closable:false,
                                                        fn:function(e){
                                                            if(e=='ok'){
                                                                Ext.getCmp('password.info').setValue('');
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        },{
                            id:'password2.info',
                            fieldLabel:"确认密码",
                            name:'password2',
                            inputType: 'password',
                            regex:/^.{8,30}$/,
                            regexText:'密码规则:8~30位!',
                            emptyText:'请输入密码!',
                            listeners:{
                                blur : function(){
                                    var password = Ext.getCmp('password.info').getValue();
                                    if(password.length>0){
                                        var password2 = this.getValue();
                                        if(password != password2&&password2.length>0){
                                            Ext.MessageBox.show({
                                                title:'信息',
                                                width:250,
                                                msg:'<font color="red">"确认密码"和"密码"不一致!</font>',
                                                animEl:'password2.info',
                                                buttons:{'ok':'确定'},
                                                icon:Ext.MessageBox.INFO,
                                                closable:false,
                                                fn:function(e){
                                                    if(e=='ok'){
                                                        Ext.getCmp('password2.info').setValue('');
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Ext.MessageBox.show({
                                            title:'信息',
                                            width:270,
                                            msg:'<font color="red">请先输入"密码",再输入"确认密码"!</font>',
                                            animEl:'password2.info',
                                            buttons:{'ok':'确定'},
                                            icon:Ext.MessageBox.INFO,
                                            closable:false,
                                            fn:function(e){
                                                if(e=='ok'){
                                                    Ext.getCmp('password2.info').setValue('');
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }*/ ,{
                            id:'ip.info',
                            fieldLabel:"客户端IP",
                            name:'clientUser.ip',
                            regex:/^(((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9]))$/,
                            regexText:'这个不是Ip',
                            emptyText:'请输入Ip',
                            value:item.data.ip
                        },{
                            id:'mac.info',
                            fieldLabel:'客户端MAC',
                            name:'clientUser.mac',
                            regex:/([0-9a-fA-F]{2})(([/\s:-][0-9a-fA-F]{2}){5})/,
                            regexText:'这个不是mac地址',
                            emptyText:'请输入网卡mac',
                            value:item.data.mac
                        }]
                });
                var win = new Ext.Window({
                    title:'修改认证账号',
                    width:500,
                    layout:'fit',
                    height:250,
                    modal:true,
                    items:formPanel,
                    bbar:[
                        '->',{
                            id:'update_win.info',
                            text:'保存',
                            handler:function(){
                                if (formPanel.form.isValid()) {
                                    formPanel.getForm().submit({
                                        url :'ClientUserAction_update.action',
                                        method :'POST',
                                        params:{id:item.data.id},
                                        waitTitle :'系统提示',
                                        waitMsg :'正在保存,请稍后...',
                                        success : function(form,action) {
                                            var msg = action.result.msg;
                                            Ext.MessageBox.show({
                                                title:'信息',
                                                width:250,
                                                msg:msg,
                                                animEl:'update_win.info',
                                                buttons:{'ok':'确定','no':'取消'},
                                                icon:Ext.MessageBox.INFO,
                                                closable:false,
                                                fn:function(e){
                                                    if(e=='ok'){
                                                        grid.render();
                                                        store.reload();
                                                        win.close();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Ext.MessageBox.show({
                                        title:'信息',
                                        width:200,
                                        msg:'请填写完成再提交!',
                                        animEl:'update_win.info',
                                        buttons:{'ok':'确定'},
                                        icon:Ext.MessageBox.ERROR,
                                        closable:false
                                    });
                                }
                            }
                        }
                    ]
                });
                win.show();
            })
        } else{
            Ext.MessageBox.show({
                title:'信息',
                width:200,
                msg:'请选择一行数据进行修改!',
                animEl:'update_win.info',
                buttons:{'ok':'确定'},
                icon:Ext.MessageBox.INFO,
                closable:false
            });
        }
    }

    function delresource(grid,store){
        var selModel = grid.getSelectionModel();
        if(selModel.hasSelection()){
            var selections = selModel.getSelections();
            Ext.each(selections,function(item){
                Ext.MessageBox.show({
                    title:'信息',
                    msg:'<font color="green">确定要删除所选记录？</font>',
                    animEl:'delete_win.info',
                    width:260,
                    buttons:{'ok':'确定','no':'取消'},
                    icon:Ext.MessageBox.INFO,
                    closable:false,
                    fn:function(e){
                        if(e == 'ok'){
                            var myMask = new Ext.LoadMask(Ext.getBody(),{
                                msg : '正在删除,请稍后...',
                                removeMask:true
                            });
                            myMask.show();
                            Ext.Ajax.request({
                                url : 'ClientUserAction_delete.action',             // 删除 连接 到后台
                                params :{id:item.data.id},
                                method:'POST',
                                success : function(r,o){
                                    myMask.hide();
                                    var respText = Ext.util.JSON.decode(r.responseText);
                                    var msg = respText.msg;
                                    Ext.MessageBox.show({
                                        title:'信息',
                                        width:250,
                                        msg:msg,
                                        animEl:'delete_win.info',
                                        buttons:{'ok':'确定'},
                                        icon:Ext.MessageBox.INFO,
                                        closable:false,
                                        fn:function(e){
                                            if(e=='ok'){
                                                grid.render();
                                                store.reload();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            });
        }
    }
});

function changePwd(){
    var grid = Ext.getCmp('clientUser');
    var store = grid.getStore();
    var selModel = grid.getSelectionModel();
    if(selModel.hasSelection()){
        var selections = selModel.getSelections();
        Ext.each(selections,function(item){
            var formPanel = new Ext.form.FormPanel({
                frame:true,
                autoScroll:true,
                labelWidth : 120, // label settings here cascade unless overridden
                border:false,
                bodyStyle : 'padding:5px 5px 0',
                width : 500,
                waitMsgTarget : true,
                defaults : {
                    width : 230
                },
                defaultType : 'textfield', //
                items:[{
                    id:'old.password.info',
                    fieldLabel:"原始密码",
                    name:'oldPassword',
                    inputType: 'password',
                    regex:/^.{8,30}$/,
                    regexText:'密码规则:8~30位!',
                    emptyText:'请输入密码!',
                    listeners:{
                        blur : function(){
                            var password = this.getValue();
                            if(password.length>0){
                                var myMask = new Ext.LoadMask(Ext.getBody(),{
                                    msg:'正在校验,请稍后...',
                                    removeMask:true
                                });
                                myMask.show();
                                Ext.Ajax.request({
                                    url : 'ClientUserAction_checkPwd.action',
                                    params :{password:password,id:item.data.id},
                                    method:'POST',
                                    success : function(r,o) {
                                        var respText = Ext.util.JSON.decode(r.responseText);
                                        var msg = respText.msg;
                                        myMask.hide();
                                        if(msg != 'true'){
                                            Ext.MessageBox.show({
                                                title:'信息',
                                                width:250,
                                                msg:msg,
                                                buttons:{'ok':'确定'},
                                                icon:Ext.MessageBox.INFO,
                                                closable:false,
                                                fn:function(e){
                                                    if(e=='ok'){
                                                        Ext.getCmp('password.info').setValue('');
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                } ,{
                    id:'password.info',
                    fieldLabel:"新密码",
                    name:'clientUser.serverPwd',
                    inputType: 'password',
                    regex:/^.{8,30}$/,
                    regexText:'密码规则:8~30位!',
                    emptyText:'请输入密码!'
                },{
                    id:'password2.info',
                    fieldLabel:"确认密码",
                    name:'password2',
                    inputType: 'password',
                    regex:/^.{8,30}$/,
                    regexText:'密码规则:8~30位!',
                    emptyText:'请输入密码!',
                    listeners:{
                        blur : function(){
                            var password = Ext.getCmp('password.info').getValue();
                            if(password.length>0){
                                var password2 = this.getValue();
                                if(password != password2&&password2.length>0){
                                    Ext.MessageBox.show({
                                        title:'信息',
                                        width:250,
                                        msg:'<font color="red">"确认密码"和"密码"不一致!</font>',
                                        animEl:'password2.info',
                                        buttons:{'ok':'确定'},
                                        icon:Ext.MessageBox.INFO,
                                        closable:false,
                                        fn:function(e){
                                            if(e=='ok'){
                                                Ext.getCmp('password2.info').setValue('');
                                            }
                                        }
                                    });
                                }
                            } else {
                                Ext.MessageBox.show({
                                    title:'信息',
                                    width:270,
                                    msg:'<font color="red">请先输入"密码",再输入"确认密码"!</font>',
                                    animEl:'password2.info',
                                    buttons:{'ok':'确定'},
                                    icon:Ext.MessageBox.INFO,
                                    closable:false,
                                    fn:function(e){
                                        if(e=='ok'){
                                            Ext.getCmp('password2.info').setValue('');
                                        }
                                    }
                                });
                            }
                        }
                    }
                }]
            });
            var win = new Ext.Window({
                title:'修改密码',
                width:500,
                layout:'fit',
                height:250,
                modal:true,
                items:formPanel,
                bbar:[
                    '->',{
                        id:'changePwd_win.info',
                        text:'保存',
                        handler:function(){
                            if (formPanel.form.isValid()) {
                                formPanel.getForm().submit({
                                    url :'ClientUserAction_update.action',
                                    method :'POST',
                                    params:{id:item.data.id},
                                    waitTitle :'系统提示',
                                    waitMsg :'正在保存,请稍后...',
                                    success : function(form,action) {
                                        var msg = action.result.msg;
                                        Ext.MessageBox.show({
                                            title:'信息',
                                            width:250,
                                            msg:msg,
                                            animEl:'changePwd_win.info',
                                            buttons:{'ok':'确定','no':'取消'},
                                            icon:Ext.MessageBox.INFO,
                                            closable:false,
                                            fn:function(e){
                                                if(e=='ok'){
                                                    grid.render();
                                                    store.reload();
                                                    win.close();
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                Ext.MessageBox.show({
                                    title:'信息',
                                    width:200,
                                    msg:'请填写完成再提交!',
                                    animEl:'changePwd_win.info',
                                    buttons:{'ok':'确定'},
                                    icon:Ext.MessageBox.ERROR,
                                    closable:false
                                });
                            }
                        }
                    }
                ]
            });
            win.show();
        });
    }
}

function setPermission(){
    var serverName = '';
    //服务
    var serverName_record = new Ext.data.Record.create([{name:'serverId',mapping:'serverId'},{name:'serverName',mapping:'serverName'}]);
    var serverName_reader = new Ext.data.JsonReader({ totalProperty:'count',root:"rows"},serverName_record);
    var serverName_store = new Ext.data.Store({
        url:"ServerUserAction_selectServerName.action",
        reader:serverName_reader
    });
    serverName_store.load();

    //服务账号
    var serverUser_record = new Ext.data.Record.create([{name:'serverId',mapping:'serverId'},{name:'serverUser',mapping:'serverUser'}]);
    var serverUser_reader = new Ext.data.JsonReader({ totalProperty:'count',root:"rows"},serverUser_record);
    var serverUser_store = new Ext.data.Store({
        url:"ServerUserAction_selectUsers.action?serverName=" + serverName  ,
        reader:serverUser_reader
    });
    serverUser_store.load({
        params:{start:0,limit:500}
    })


    //显示当前认证用户拥有的权限
    var grid_panel = Ext.getCmp('clientUser');
    var selModel = grid_panel.getSelectionModel();
    if(selModel.hasSelection()){
        var selections = selModel.getSelections();
        Ext.each(selections,function(item){
            var resourceRecord = Ext.data.Record.create(
                [{name: 'serverId',mapping:'serverId'},
                    {name:'serverName',mapping:'serverName'},
                    {name:'serverType',mapping:'serverType'},
                    {name:'ipPort',mapping:'ipPort'},
                    {name: 'serverUser',mapping:'serverUser'},
                    {name: 'serverPwd',mapping:'serverPwd'},
                    {name: 'permission',mapping:'permission'},
                    {name: 'dir',mapping:'dir'}
                ]);
            var store = new Ext.data.Store({
                proxy: new Ext.data.HttpProxy({url:'ClientUserAction_selectServers.action',method:'POST'}),
                reader: new Ext.data.JsonReader({
                    totalProperty:"count",
                    root: 'rows'
                }, resourceRecord)
            });
            store.load({
                params:{clientId:item.data.id}});
            var rowNumber = new Ext.grid.RowNumberer();         //自动 编号
            var resourceCm = new Ext.grid.ColumnModel([
                rowNumber,
                {header: "服务名称", width: 70,align:'center', dataIndex: 'serverName',sortable:true,menuDisabled:true},
                {header: "服务类型", width: 70,align:'center', dataIndex: 'serverType',sortable:true,menuDisabled:true,renderer:typeState},
                {header: "IP端口", width: 120,align:'center', dataIndex: 'ipPort',sortable:true,menuDisabled:true},
                {header: "服务账号", width: 70,align:'center', dataIndex: 'serverUser',sortable:true,menuDisabled:true},
                {header: "服务密码", width: 70,align:'center', dataIndex: 'serverPwd',sortable:true,menuDisabled:true},
                {header: "服务权限", width: 70,align:'center', dataIndex: 'permission',sortable:true,menuDisabled:true,renderer:userPermission},
                {header: "权限路径", width: 100, dataIndex: 'dir',align:'center',sortable:true,menuDisabled:true},
                {
                    header:"操作",
                    dataIndex: '0',
                    align:"center",
                    sortable : false,
                    width:120,
                    renderer:function(v){
                        return "<a href='javascript:;' onclick='deletePermission(" +item.data.id+ ")'>删除</a>";
                    }
                }
            ]);

            // create the grid
            var grid = new Ext.grid.GridPanel({
                id:'clientServer.grid',
                store: store,
                cm: resourceCm,
                viewConfig: {
                    forceFit:true
                },
                columnLines: true,
                autoScroll:true,
                loadMask:{msg:'正在加载数据，请稍后...'},
                border:false,
                collapsible:false,
                stripeRows:true,
                autoExpandColumn:'Position',
                enableHdMenu:true,
                enableColumnHide:true,
//        bodyStyle:'width:100%',
                selModel:new Ext.grid.RowSelectionModel({singleSelect:false}),
                height:300,
                frame:true,
                iconCls:'icon-grid',

                // inline toolbars
                tbar:['服务名称',{
                    id:'auth.tbar.serverName',
                    fieldLabel:'服务名称',
                    hiddenName:'serverNameId',
                    xtype:'combo',
                    model:'remote',
//                editable:false,
                    typeAhead:true,
                    forceSelection: true,
                    triggerAction:'all',
                    displayField:"serverName",valueField:"serverName" ,
                    store:serverName_store,
                    listeners:{
                        'select':function(){
                            serverName = this.getValue();
                            Ext.getCmp('auth.tbar.serverUser').clearValue();
                            serverUser_store.proxy = new Ext.data.HttpProxy({url:"ServerUserAction_selectUsers.action?serverName=" + serverName});
                            serverUser_store.reload();
                        }
                    }

                },'-','服务账号',{
                    id:'auth.tbar.serverUser',
                    fieldLabel:'服务账号',
                    hiddenName:'serverUserId',
                    xtype:'combo',
                    model:'remote',
//                editable:false,
                    typeAhead:true,
                    forceSelection: false,
                    triggerAction:'all',
                    displayField:"serverUser",valueField:"serverId" ,
                    store:serverUser_store

                },{
                    text: '新增权限',
                    iconCls:'add',
                    handler: function(){
                        var serverId = Ext.getCmp('auth.tbar.serverUser').getValue();
                        if(serverId > 0){
                            Ext.MessageBox.show({
                                title:'信息',
                                msg:'<font color="green">确定要添加权限?</font>',
                                animEl:'add_permission.info',
                                width:260,
                                buttons:{'ok':'确定','no':'取消'},
                                icon:Ext.MessageBox.INFO,
                                closable:false,
                                fn:function(e){
                                    if(e == 'ok'){
                                        var myMask = new Ext.LoadMask(Ext.getBody(),{
                                            msg : '正在添加,请稍后...',
                                            removeMask:true
                                        });
                                        myMask.show();
                                        Ext.Ajax.request({
                                            url : 'ServerUserAction_insertClient.action',             // 删除 连接 到后台
                                            params :{id:serverId,clientId:item.data.id},
                                            method:'POST',
                                            success : function(r,o){
                                                myMask.hide();
                                                var respText = Ext.util.JSON.decode(r.responseText);
                                                var msg = respText.msg;
                                                Ext.MessageBox.show({
                                                    title:'信息',
                                                    width:250,
                                                    msg:msg,
                                                    animEl:'add_permission.info',
                                                    buttons:{'ok':'确定'},
                                                    icon:Ext.MessageBox.INFO,
                                                    closable:false,
                                                    fn:function(e){
                                                        if(e=='ok'){
                                                            grid.render();
                                                            store.reload();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }else{
                            Ext.MessageBox.show({
                                title:'信息',
                                width:200,
                                msg:'请选择服务账号后再提交!',
                                animEl:'clientServer.grid',
                                buttons:{'ok':'确定'},
                                icon:Ext.MessageBox.ERROR,
                                closable:false
                            });
                        }
                    }
                }]
            });
            var win = new Ext.Window({
                title:'服务账号',
                width:800,
                layout:'fit',
                height:300,
                modal:true,
                items:grid
            }).show();
        });
    }
}

function deletePermission(clientId){
    var grid_panel = Ext.getCmp('clientServer.grid');
    var selModel = grid_panel.getSelectionModel();
    if(selModel.hasSelection()){
        var selections = selModel.getSelections();
        Ext.each(selections,function(item){
            Ext.MessageBox.show({
                title:'信息',
                msg:'<font color="green">确定要删除权限?</font>',
                animEl:'delete_permission.info',
                width:260,
                buttons:{'ok':'确定','no':'取消'},
                icon:Ext.MessageBox.INFO,
                closable:false,
                fn:function(e){
                    if(e == 'ok'){
                        var myMask = new Ext.LoadMask(Ext.getBody(),{
                            msg : '正在删除,请稍后...',
                            removeMask:true
                        });
                        myMask.show();
                        Ext.Ajax.request({
                            url : 'ServerUserAction_deleteClient.action',             // 删除 连接 到后台
                            params :{id:item.data.serverId,clientId:clientId},
                            method:'POST',
                            success : function(r,o){
                                myMask.hide();
                                var respText = Ext.util.JSON.decode(r.responseText);
                                var msg = respText.msg;
                                Ext.MessageBox.show({
                                    title:'信息',
                                    width:250,
                                    msg:msg,
                                    animEl:'delete_permission.info',
                                    buttons:{'ok':'确定'},
                                    icon:Ext.MessageBox.INFO,
                                    closable:false,
                                    fn:function(e){
                                        if(e=='ok'){
                                            grid_panel.render();
                                            grid_panel.getStore().reload();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        });

    }else{
        Ext.MessageBox.show({
            title:'信息',
            width:200,
            msg:'请选择一条记录!',
            buttons:{'ok':'确定'},
            icon:Ext.MessageBox.INFO,
            closable:false
        });
    }

}

function typeState(v){
    if(v == 0){
        return 'ftp';
    }else{
        return 'smb';
    }
}

function userPermission(v){
    if(v == 0){
        return '只读';
    }else if(v == 1){
        return '改名';
    }else if(v ==2){
        return '追加';
    }else if(v == 3){
        return '删除';
    }else{
        return '';
    }
}