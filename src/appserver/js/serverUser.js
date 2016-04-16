var start = 0;
var pageSize = 15;
Ext.onReady(function(){

    var currentRow = null;


    var resourceRecord = Ext.data.Record.create(
        [
            {name: 'id',mapping:'id'},
            {name:'serverName',mapping:'serverName'},
            {name: 'serverType',mapping:'serverType'},
            {name: 'ipPort',mapping:'ipPort'},
            {name: 'createTime',mapping:'createTime'}
        ]);

    var store = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'ServerUserAction_select.action',method:'POST'}),

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

    var rowNumber = new Ext.grid.RowNumberer();         //自动 编号
    var resourceCm = new Ext.grid.ColumnModel([
        rowNumber,
        {header: "服务编号", width: 50,align:'center', dataIndex: 'id',sortable:true,menuDisabled:true},
        {header: "服务名称", width: 50,align:'center', dataIndex: 'serverName',sortable:true,menuDisabled:true},
        {header: "服务类型", width: 100, dataIndex: 'serverType',align:'center',sortable:true,menuDisabled:true,renderer:typeState},
        {header: "IP端口", width: 100, dataIndex: 'ipPort',align:'center',sortable:true,menuDisabled:true},
        {header: "创建时间", width: 100, dataIndex: 'createTime',align:'center',sortable:true,menuDisabled:true},
        {
            header:"操作",
            dataIndex: '0',
            align:"center",
            sortable : false,
            width:100,
            renderer:function(v){
                return "<a href='javascript:;' onclick='getServerUser()'>服务账号</a>  <a href='javascript:;' onclick='startnow()'></a>";
            }
        }
    ]);

    // create the grid
    var grid = new Ext.grid.GridPanel({
        id:'serverName.grid',
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
        tbar:[{
            text: '新增服务',
            iconCls:'add',
            handler: function(){addServer(grid,store);}
        },'-',{
            text: '编辑服务',
            iconCls:'upgrade',
            handler: function(){editresource(grid,store);}
        },'-',{
            text: '删除服务',
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

    function typeState(v){
        if(v == 0){
            return 'ftp';
        }else{
            return 'smb';
        }
    }


    var dataStatus = [['0','ftp'],['1','smb']];
    var storeStatus = new Ext.data.SimpleStore({fields:['value','key'],data:dataStatus});

    function addServer(grid,store){
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
                    id:'insert.serverName',
                    fieldLabel:"服务名称",
                    name:'serverUser.serverName',
                    regex:/^.{2,30}$/,
                    regexText:'请输入任意2--30个字符',
                    emptyText:'请输入任意2--30个字符',
                    listeners:{
                        blur:function(){
                            var serverName = this.getValue();
                            if(  serverName.length > 0){
                                var myMask = new Ext.LoadMask(Ext.getBody(),{
                                    msg:'正在校验,请稍后...',
                                    removeMask:true
                                });
                                myMask.show();
                                Ext.Ajax.request({
                                    url : 'ServerUserAction_checkServerName.action',
                                    params :{serverName:serverName},
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
                                                        Ext.getCmp('insert.serverName').setValue('');
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
                    fieldLabel:"服务类型",
                    hiddenName:'serverUser.serverType',
                    xtype:'combo',
                    mode:'local',
                    emptyText :'--请选择--',
                    editable : false,
                    typeAhead:true,
                    forceSelection: true,
                    triggerAction:'all',
                    displayField:"key",valueField:"value",
                    store:storeStatus
                },{
                    id:'insert.ipPort',
                    fieldLabel:"IP端口",
                    name:'serverUser.ipPort',
                    regex:/^(((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])):([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/,
                    regexText:'请输入正确的IP端口,格式为 ip:port',
                    emptyText:'请输入正确的IP端口,格式为 ip:port',
                    listeners:{
                        blur:function(){
                            var ipPort = this.getValue();
                            if(  ipPort.length > 0){
                                var myMask = new Ext.LoadMask(Ext.getBody(),{
                                    msg:'正在校验,请稍后...',
                                    removeMask:true
                                });
                                myMask.show();
                                Ext.Ajax.request({
                                    url : 'ServerUserAction_checkIpport.action',
                                    params :{ipport:ipPort},
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
                                                        Ext.getCmp('insert.ipPort').setValue('');
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                }]
        });
        var win = new Ext.Window({
            title:'新增服务',
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
                                url :'ServerUserAction_insert.action',
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
                        name:'serverUser.id',
                        xtype:'hidden',
                        value:item.data.id
                    },
                        {
                            id:'update.serverName',
                            fieldLabel:"服务名称",
                            name:'serverUser.serverName',
                            regex:/^.{2,30}$/,
                            regexText:'请输入任意2--30个字符',
                            emptyText:'请输入任意2--30个字符',
                            value:item.data.serverName,
                            listeners:{
                                blur:function(){
                                    var serverName = this.getValue();
                                    if(  serverName.length > 0 && !(serverName.indexOf(item.data.serverName) > -1 && item.data.serverName.indexOf(serverName) > -1)){
                                        var myMask = new Ext.LoadMask(Ext.getBody(),{
                                            msg:'正在校验,请稍后...',
                                            removeMask:true
                                        });
                                        myMask.show();
                                        Ext.Ajax.request({
                                            url : 'ServerUserAction_checkServerName.action',
                                            params :{serverName:serverName},
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
                                                                Ext.getCmp('update.serverName').setValue(item.data.serverName);
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
                            fieldLabel:"服务类型",
                            hiddenName:'serverUser.serverType',
                            xtype:'combo',
                            mode:'local',
                            emptyText :'--请选择--',
                            editable : false,
                            typeAhead:true,
                            forceSelection: true,
                            triggerAction:'all',
                            displayField:"key",valueField:"value",
                            store:storeStatus ,
                            value:item.data.serverType
                        },{
                            id:'update.ipPort',
                            fieldLabel:"IP端口",
                            name:'serverUser.ipPort',
                            regex:/^(((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])):([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/,
                            regexText:'请输入正确的IP端口,格式为 ip:port',
                            emptyText:'请输入正确的IP端口,格式为 ip:port',
                            value:item.data.ipPort,
                            listeners:{
                                blur:function(){
                                    var ipPort = this.getValue();
                                    if(  ipPort.length > 0){
                                        var myMask = new Ext.LoadMask(Ext.getBody(),{
                                            msg:'正在校验,请稍后...',
                                            removeMask:true
                                        });
                                        myMask.show();
                                        Ext.Ajax.request({
                                            url : 'ServerUserAction_checkIpport.action',
                                            params :{ipport:ipPort},
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
                                                                Ext.getCmp('update.ipPort').setValue('');
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }]
                });
                var win = new Ext.Window({
                    title:'修改服务信息',
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
                                        url :'ServerUserAction_update.action',
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
                                url : 'ServerUserAction_delete.action',             // 删除 连接 到后台
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

/**
 * 获取服务的所有账号及权限
 */
function getServerUser(){
    var grid_panel = Ext.getCmp('serverName.grid');
    var selModel = grid_panel.getSelectionModel();
    if(selModel.hasSelection()){
        var selections = selModel.getSelections();
        Ext.each(selections,function(item){
            var resourceRecord = Ext.data.Record.create(
                [{name: 'serverId',mapping:'serverId'},
                    {name:'serverName',mapping:'serverName'},
                    {name: 'serverUser',mapping:'serverUser'},
                    {name: 'permission',mapping:'permission'},
//                    {name: 'dir',mapping:'dir'},
                    {name: 'createTime',mapping:'createTime'}
                ]);
            var store = new Ext.data.Store({
                proxy: new Ext.data.HttpProxy({url:'ServerUserAction_selectUsers.action?serverName=' + item.data.serverName,method:'POST'}),

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
            var rowNumber = new Ext.grid.RowNumberer();         //自动 编号
            var resourceCm = new Ext.grid.ColumnModel([
                rowNumber,
                {header: "服务账号", width: 60,align:'center', dataIndex: 'serverUser',sortable:true,menuDisabled:true},
                {header: "服务权限", width: 60,align:'center', dataIndex: 'permission',sortable:true,menuDisabled:true,renderer:userPermission},
//                {header: "权限路径", width: 100, dataIndex: 'dir',align:'center',sortable:true,menuDisabled:true},
                {header: "创建时间", width: 120, dataIndex: 'createTime',align:'center',sortable:true,menuDisabled:true},
                {
                    header:"操作",
                    dataIndex: '0',
                    align:"center",
                    sortable : false,
                    width:120,
                    renderer:function(v){
                        return "<a href='javascript:;' onclick='updateServerUser()'>修改</a>  <a href='javascript:;' onclick='changePwd()'>修改密码</a> <a href='javascript:;' onclick='getClients()'>分配权限</a> <a href='javascript:;' onclick='deleteServerUser()'>删除</a>";
                    }
                }
            ]);


            var page_toolbar = new Ext.PagingToolbar({
                pageSize:pageSize,
                store:store,
                displayInfo:true,
                displayMsg:"显示第{0}条记录到第{1}条记录，一共{2}条",
                emptyMsg:"没有记录",
                beforePageText:"当前页",
                afterPageText:"共{0}页"
            });

            // create the grid
            var grid = new Ext.grid.GridPanel({
                id:'serverUser.grid',
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
                tbar:[{
                    text: '新增账号',
                    iconCls:'add',
                    handler: function(){addServerUser(grid,store,item)}
                }],
                bbar:page_toolbar
            });
            var win = new Ext.Window({
                title:'服务账号',
                width:650,
                layout:'fit',
                height:300,
                modal:true,
                items:grid
            }).show();
        });
    }
}

function addServerUser(grid,store,item){
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
                id:'insert.serverUser',
                fieldLabel:"服务账号",
                name:'serverUser.serverUser',
                regex:/^.{2,30}$/,
                regexText:'请输入任意2--30个字符',
                emptyText:'请输入任意2--30个字符',
                listeners:{
                    blur:function(){
                        var serverUser = this.getValue();
                        if(  serverUser.length > 0){
                            var myMask = new Ext.LoadMask(Ext.getBody(),{
                                msg:'正在校验,请稍后...',
                                removeMask:true
                            });
                            myMask.show();
                            Ext.Ajax.request({
                                url : 'ServerUserAction_checkServerUser.action',
                                params :{userName:serverUser,serverName:item.data.serverName},
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
                                                    Ext.getCmp('insert.serverUser').setValue('');
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
                fieldLabel:"服务密码",
                name:'serverUser.serverPwd',
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
            },{
                fieldLabel:"服务权限",
                hiddenName:'serverUser.permission',
                xtype:'combo',
                mode:'local',
                emptyText :'--请选择--',
                editable : false,
                typeAhead:true,
                forceSelection: true,
                triggerAction:'all',
                displayField:"key",valueField:"value",
                store:storePermission
            }/*,{
                fieldLabel:"权限路径",
                name:'serverUser.dir',
                regex:/^.{2,30}$/,
                regexText:'请输入任意2--30个字符',
                emptyText:'请输入任意2--30个字符'
            }*/]
    });
    var win = new Ext.Window({
        title:'新增账号',
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
                            url :'ServerUserAction_insertUser.action',
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

function updateServerUser(){
    var grid = Ext.getCmp('serverUser.grid');
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
                items:[
                    {
                        id:'update.serverUser',
                        fieldLabel:"服务账号",
                        name:'serverUser.serverUser',
                        regex:/^.{2,30}$/,
                        regexText:'请输入任意2--30个字符',
                        emptyText:'请输入任意2--30个字符',
                        value:item.data.serverUser,
                        allowBlank:false,
                        listeners:{
                            blur:function(){
                                var serverUser = this.getValue();
                                if(  serverUser.length > 0 && !(serverUser.indexOf(item.data.serverUser) > -1 && item.data.serverUser.indexOf(serverUser) > -1)){
                                    var myMask = new Ext.LoadMask(Ext.getBody(),{
                                        msg:'正在校验,请稍后...',
                                        removeMask:true
                                    });
                                    myMask.show();
                                    Ext.Ajax.request({
                                        url : 'ServerUserAction_checkServerUser.action',
                                        params :{userName:serverUser,id:item.data.serverId},
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
                                                            Ext.getCmp('insert.serverUser').setValue('');
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
                        fieldLabel:"服务密码",
                        name:'serverUser.serverPwd',
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
                    },{
                        fieldLabel:"服务权限",
                        hiddenName:'serverUser.permission',
                        xtype:'combo',
                        mode:'local',
                        emptyText :'--请选择--',
                        editable : false,
                        typeAhead:true,
                        forceSelection: true,
                        triggerAction:'all',
                        displayField:"key",valueField:"value",
                        store:storePermission ,
                        value:item.data.permission ,
                        allowBlank:false
                    },{
                        fieldLabel:"权限路径",
                        name:'serverUser.dir',
                        regex:/^.{2,30}$/,
                        regexText:'请输入任意2--30个字符',
                        emptyText:'请输入任意2--30个字符',
                        value:item.data.dir,
                        allowBlank:false
                    }]
            });
            var win = new Ext.Window({
                title:'修改账号',
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
                                    url :'ServerUserAction_updateUser.action',
                                    method :'POST',
                                    params:{id:item.data.serverId},
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
        });
    }
}

function changePwd(){
    var grid = Ext.getCmp('serverUser.grid');
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
                                    url : 'ServerUserAction_checkPwd.action',
                                    params :{password:password,id:item.data.serverId},
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
                        fieldLabel:"服务密码",
                        name:'serverUser.serverPwd',
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
                                    url :'ServerUserAction_updateUser.action',
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

function deleteServerUser(){
    var grid = Ext.getCmp('serverUser.grid');
    var store = grid.getStore();
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
                            url : 'ServerUserAction_delete.action',             // 删除 连接 到后台
                            params :{id:item.data.serverId},
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

function getClients(){
    var grid_panel = Ext.getCmp('serverUser.grid');
    var selModel = grid_panel.getSelectionModel();
    if(selModel.hasSelection()){
        var selections = selModel.getSelections();
        Ext.each(selections,function(item){
            var resourceRecord = Ext.data.Record.create(
                [
                    {name: 'id1',mapping:'id1'},
                    {name:'serverName',mapping:'serverName'},
                    {name: 'clientId',mapping:'clientId'},
                    {name: 'userName',mapping:'userName'},
                    {name: 'ip',mapping:'ip'},
                    {name: 'mac',mapping:'mac'},
                    {name: 'connectTime',mapping:'connectTime'}
                ]);
            var store = new Ext.data.Store({
                proxy: new Ext.data.HttpProxy({url:'ServerUserAction_selectClients.action?id=' + item.data.serverId,method:'POST'}),

                reader: new Ext.data.JsonReader({
                    totalProperty:"count",
                    root: 'rows'
                }, resourceRecord)
            });
            store.load({
                params:{
                    start:start,limit:pageSize
                }
            });
            var rowNumber = new Ext.grid.RowNumberer();         //自动 编号
            var resourceCm = new Ext.grid.ColumnModel([
                rowNumber,
                {header: "客户端账号", width: 80,align:'center', dataIndex: 'userName',sortable:true,menuDisabled:true},
                {header: "客户端IP", width: 100,align:'center', dataIndex: 'ip',sortable:true,menuDisabled:true},
                {header: "客户端MAC", width: 100, dataIndex: 'mac',align:'center',sortable:true,menuDisabled:true},
                {header: "连接时间", width: 100, dataIndex: 'connectTime',align:'center',sortable:true,menuDisabled:true},
                {
                    header:"操作",
                    dataIndex: '0',
                    align:"center",
                    sortable : false,
                    width:50,
                    renderer:function(v){
                        return "<a href='javascript:;' onclick='deleteClient()'>删除</a>";
                    }
                }
            ]);

            // create the grid
            var grid = new Ext.grid.GridPanel({
                id:'serverClient.grid',
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
                enableColumnHide:true,
//        bodyStyle:'width:100%',
                selModel:new Ext.grid.RowSelectionModel({singleSelect:false}),
                height:300,
                plain:true,
                iconCls:'icon-grid',

                // inline toolbars
                tbar:[{
                    text: '新增账号',
                    iconCls:'add',
                    handler: function(){addClient(item.data.serverId)}
                }]
            });
            var win = new Ext.Window({
                title:'客户端账号',
                width:650,
                layout:'fit',
                height:300,
                modal:true,
                items:grid
            }).show();
        });
    }
}

function addClient(id){
    var name_record = new Ext.data.Record.create([{name:'value',mapping:'value'},{name:'key',mapping:'key'}]);
    var name_reader = new Ext.data.JsonReader({ totalProperty:'count',root:"rows"},name_record);
    var name_store = new Ext.data.Store({
        url:"ServerUserAction_selectClientName.action?id=" + id,
        reader:name_reader
    });
    var formPanel = new Ext.form.FormPanel({
        frame:true,
        width:800,
        autoScroll:true,
        baseCls : 'x-plain',
        labelWidth:120,
        labelAlign:'right',
        defaultWidth:200,
        layout:'form',
        border:false,
        defaults:{
            width:250,
            xtype:'textfield'
        },
        items:[
            {
                id:'insert.client',
                fieldLabel:'客户端',
                hiddenName:'clientId',
                xtype:'combo',
                model:'local',
                typeAhead:true,
                forceSelection: true,
                triggerAction:'all',
                displayField:"key",valueField:"value" ,
                store:name_store
            }
        ]
    });
    var select_Win = new Ext.Window({
        title:"新增认证客户端",
        width:420,
        layout:'fit',
        height:100,
        modal:true,
        items:formPanel ,
        bbar:[
            '->',{
                id:'update_win.info',
                text:'保存',
                width:50 ,
                handler:function(){
                    if (formPanel.form.isValid()) {
                        formPanel.getForm().submit({
                            url :'ServerUserAction_insertClient.action',
                            timeout: 20*60*1000,
                            method :'POST',
                            waitTitle :'系统提示',
                            params:{
                                id:id
                            },
                            waitMsg :'正在连接...',
                            success : function(form, action) {
                                var msg = action.result.msg;
                                Ext.MessageBox.show({
                                    title:'信息',
                                    width:250,
                                    msg:msg,
                                    buttons:Ext.MessageBox.OK,
                                    buttons:{'ok':'确定'},
                                    icon:Ext.MessageBox.INFO,
                                    closable:false,
                                    fn:function(e){
                                        Ext.getCmp('serverUser.grid').getStore().reload();
                                        select_Win.close();
                                    }
                                });
                            },
                            failure : function() {
                                Ext.MessageBox.show({
                                    title:'信息',
                                    width:250,
                                    msg:'更新失败，请与管理员联系!',
                                    buttons:Ext.MessageBox.OK,
                                    buttons:{'ok':'确定'},
                                    icon:Ext.MessageBox.ERROR,
                                    closable:false
                                });
                            }
                        });
                    } else {
                        Ext.MessageBox.show({
                            title:'信息',
                            width:200,
                            msg:'请填写完成再提交!',
                            buttons:Ext.MessageBox.OK,
                            buttons:{'ok':'确定'},
                            icon:Ext.MessageBox.ERROR,
                            closable:false
                        });
                    }
                }
            }, {
                text:'取消',
                width:50,
                handler:function(){
                    select_Win.close();
                }
            }
        ]
    });
    select_Win.show();

}

function deleteClient(){
    var grid_panel = Ext.getCmp('serverClient.grid');
    var selModel = grid_panel.getSelectionModel();
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
                            url : 'ServerUserAction_deleteClient.action',             // 删除 连接 到后台
                            params :{id:item.data.id1,clientId:item.data.clientId},
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
    }
}


var dataPermission = [['0','只读'],['1','改名'],['2','追加'],['3','删除']];
var storePermission = new Ext.data.SimpleStore({fields:['value','key'],data:dataPermission});