
Ext.onReady(function(){

    var currentRow = null;

    var start = 0;
    var pageSize = 15;





    var record = new Ext.data.Record.create([
        {name:'clientUser',mapping:'clientUser'},
        {name:'clientPwd',mapping:'clientPwd'},
        {name:'serverIp',mapping:'serverIp'},
        {name:'serverPort',mapping:'serverPort'},
        {name:'clientIp',mapping:'clientIp'},
        {name:'clientPort',mapping:'clientPort'},
        {name:'connect',mapping:'connect'}
    ]);
    var proxy = new Ext.data.HttpProxy({
        url:"ClientAction_getClient.action"
    });
    var reader = new Ext.data.JsonReader({
        root:"rows"
    },record);
    var store = new Ext.data.Store({
        proxy : proxy,
        reader : reader
    });

    var ip_record = new Ext.data.Record.create([{name:'ip',mapping:'ip'}]);
    var ip_reader = new Ext.data.JsonReader({ totalProperty:'count',root:"rows"},ip_record);
    var ip_store = new Ext.data.Store({
        url:"ClientAction_getIps.action",
        reader:ip_reader
    });
    ip_store.load();



    var form_panel = new Ext.form.FormPanel({
        id:'port.info',
        plain:true,
        buttonAlign :'left',
        labelAlign:'right',
        labelWidth:120,
        items:[{
            layout:'column',
            items:[{
                columnWidth:.5,
                layout:'form',
                defaults:{
                    width:200
                },
                items:[{
                    id:'server.serverIp.info',
                    xtype:'textfield',
                    name:'serverIp',
                    fieldLabel : '服务端地址',
                    allowBlank:false,
                    listeners:{
                        blur : function(){
                            Ext.getCmp('connect').disable();

                        }
                    }
                },{
                    id:'server.serverPort.info',
                    xtype:'textfield',
                    name:'serverPort',
                    fieldLabel : '服务端端口',
                    allowBlank:false,
                    listeners:{
                        blur : function(){
                            Ext.getCmp('connect').disable();

                        }
                    }
                },{
                    id:'client.clientIp.info',
                    fieldLabel:"服务地址",
                    hiddenName:'clientIp',
                    xtype:'combo',
                    mode:'remote',
                    emptyText:'--请选择--',
                    editable:false,
                    typeAhead:true,
                    forceSelection: true,
                    triggerAction:'all',
                    displayField:"ip",valueField:"ip",
                    store:ip_store,
                    listeners:{
                        blur : function(){
                            Ext.getCmp('connect').disable();

                        }
                    }
                },{
                    id:'client.clientPort.info',
                    xtype:'textfield',
                    name:'clientPort',
                    fieldLabel : '客户端端口',
                    allowBlank:false,
                    listeners:{
                        blur : function(){
                            var clientPort = this.getValue();
                            var serverPort = Ext.getCmp('server.serverPort.info').getValue();
                            if(clientPort.length > 0 && serverPort.length > 0 &&  clientPort.indexOf(serverPort) > -1 && serverPort.indexOf(clientPort) > -1){
                                Ext.Msg.show({title : '错误',msg :'客户端端口不能与服务端端口相同。',buttons:{'ok':'确定'}});
                                Ext.getCmp('client.clientPort.info').setValue("");
                            }
                            Ext.getCmp('connect').disable();

                        }
                    }
                },{
                    id:'client.clientUser.info',
                    xtype:'textfield',
                    name:'clientUser',
                    fieldLabel : '认证账号',
                    allowBlank:false,
                    listeners:{
                        blur : function(){
                            Ext.getCmp('connect').disable();

                        }
                    }
                },{
                    id:'client.clientPwd.info',
                    xtype:'textfield',
                    name:'clientPwd',
                    fieldLabel : '认证密码',
                    inputType: 'password',    //密码
                    allowBlank:false,
                    listeners:{
                        blur : function(){
                            Ext.getCmp('connect').disable();

                        }
                    }
                }]
            },{
                columnWidth:.5,
                layout:'form',
                items:[{
                    id:'client.state',
                    xtype:'displayfield',
                    fieldLabel:'当前状态',
//                    html:'<font color=red size="3">离线</font>'
//                    text:'',
//                    style:'font-size:14px;color:red;'
                    value:'<font color=red size="3">离线</font>'
                }]
            }]
        }
        ],
        buttons:[
            new Ext.Toolbar.Spacer({width:100}),{
                id:'saveproperty',
                text:'保存配置',
                handler:function(){
                    Ext.Msg.confirm('提示', '您确定要更新配置吗?', SaveDS, Ext.MessageBox.buttonText.yes = "确认", Ext.MessageBox.buttonText.no = "取消");
                }
            },{
                id:'connect',
                text:'认证',
                handler:function(){
                    Ext.Msg.confirm('提示', '您确定要与服务端认证吗?', Connect, Ext.MessageBox.buttonText.yes = "确认", Ext.MessageBox.buttonText.no = "取消");
                }
            },{
                id:'disconnect',
                text:'取消认证',
                disabled:true,
                handler:function(){
                    Ext.Msg.confirm('提示', '您确定要与服务端断开认证吗?', Disconnect, Ext.MessageBox.buttonText.yes = "确认", Ext.MessageBox.buttonText.no = "取消");
                }
            }]
    });



    function SaveDS(btn) {
        if (btn == 'yes'){
            if(form_panel.getForm().isValid()){
                form_panel.getForm().submit({
                    clientValidation : true,
                    url : 'ClientAction_setClient.action',
                    success : function(form, action) {
                        Ext.Msg.show({title : '提示',msg :'保存成功',buttons:{'ok':'确定'}});
                        Ext.getCmp('connect').enable();

                        store.load();
                    },
                    failure : function(form, action) {
                        Ext.Msg.show({title : '错误',msg :'系统错误，请联系管理员。',buttons:{'ok':'确定'}});
                    }
                });
            }else{
                Ext.MessageBox.show({
                    title:'信息',
                    width:200,
                    msg:'请填写完成再提交!',
                    animEl:'port.info',
                    buttons:{'ok':'确定'},
                    icon:Ext.MessageBox.ERROR,
                    closable:false
                });
            }
        }
    }

    function Connect(btn){
        if (btn == 'yes'){
            form_panel.getForm().submit({
                clientValidation : true,
                url : 'ClientAction_connect.action',
                success : function(form, action) {
                    var msg = action.result.msg;
                    Ext.Msg.show({title : '提示',msg :msg,buttons:{'ok':'确定'}});
                    var myMask = new Ext.LoadMask(Ext.getBody(),{
                        msg:'正在连接,请稍后...',
                        removeMask:true
                    });
                    myMask.show();
                    serverstore.load();
                    Ext.getCmp('disconnect').enable(true);
                    Ext.getCmp('connect').disable();
                    Ext.getCmp('saveproperty').disable();
                    Ext.getCmp('server.serverIp.info').disable(true);
                    Ext.getCmp('server.serverPort.info').disable(true);
                    Ext.getCmp('client.clientIp.info').disable(true);
                    Ext.getCmp('client.clientPort.info').disable(true);
                    Ext.getCmp('client.clientUser.info').disable(true);
                    Ext.getCmp('client.clientPwd.info').disable(true);
//                    Ext.getCmp('client.state').setText('在线');
                    Ext.getCmp('client.state').setValue('<font color=green size="3">在线</font> ');
                    myMask.hide();
                },
                failure : function(form, action) {
                    var msg = "认证失败!";
                    if(action.result.msg != null){
                        msg = action.result.msg;

                    }
//                    Ext.getCmp('client.state').setText('离线');
                    Ext.getCmp('client.state').setValue('<font color=red size="3">离线</font> ');

                    Ext.Msg.show({title : '错误',msg :msg,buttons:{'ok':'确定'}});
                }
            });
        }
    }

    function Disconnect(btn){
        if(btn == 'yes'){
            form_panel.getForm().submit({
                clientValidation : true,
                url : 'ClientAction_disconnect.action',
                success : function(form, action) {
                    Ext.Msg.show({title : '提示',msg :'取消成功',buttons:{'ok':'确定'}});
                    Ext.getCmp('disconnect').disable();
                    Ext.getCmp('connect').enable(true);
                    Ext.getCmp('saveproperty').enable(true);
                    Ext.getCmp('server.serverIp.info').enable(true);
                    Ext.getCmp('server.serverPort.info').enable(true);
                    Ext.getCmp('client.clientIp.info').enable(true);
                    Ext.getCmp('client.clientPort.info').enable(true);
                    Ext.getCmp('client.clientUser.info').enable(true);
                    Ext.getCmp('client.clientPwd.info').enable(true);
                    serverstore.load();
                },
                failure : function(form, action) {
                    Ext.Msg.show({title : '错误',msg :'系统错误，请联系管理员。',buttons:{'ok':'确定'}});
                }
            });
        }
    }

    var server_resourceRecord = Ext.data.Record.create(
        [{name: 'serverId',mapping:'serverId'},
            {name:'serverName',mapping:'serverName'},
            {name:'serverType',mapping:'serverType'},
            {name:'ipPort',mapping:'ipPort'},
            {name: 'serverUser',mapping:'serverUser'},
//            {name: 'serverPwd',mapping:'serverPwd'},
            {name: 'permission',mapping:'permission'},
            {name: 'dir',mapping:'dir'}
        ]);
    var serverstore = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'ClientAction_selectServers.action',method:'POST'}),
        reader: new Ext.data.JsonReader({
            totalProperty:"count",
            root: 'rows'
        }, server_resourceRecord)
    });

    serverstore.load();
    var rowNumber = new Ext.grid.RowNumberer();         //自动 编号
    var resourceCm = new Ext.grid.ColumnModel([
        rowNumber,
        {header: "服务名称", width: 70,align:'center', dataIndex: 'serverName',sortable:true,menuDisabled:true},
        {header: "服务类型", width: 70,align:'center', dataIndex: 'serverType',sortable:true,menuDisabled:true,renderer:typeState},
        {header: "服务地址", width: 120,align:'center', dataIndex: 'ipPort',sortable:true,menuDisabled:true},
        {header: "服务账号", width: 70,align:'center', dataIndex: 'serverUser',sortable:true,menuDisabled:true},
//        {header: "服务密码", width: 70,align:'center', dataIndex: 'serverPwd',sortable:true,menuDisabled:true},
        {header: "服务权限", width: 70,align:'center', dataIndex: 'permission',sortable:true,menuDisabled:true,renderer:userPermission},
        {header: "权限路径", width: 100, dataIndex: 'dir',align:'center',sortable:true,menuDisabled:true},
        {
            header:"操作",
            dataIndex: '0',
            align:"center",
            sortable : false,
            width:120,
            renderer:function(v){
                return "<a href='javascript:;'  onclick='getPwd()'>查看密码</a>";// <a href='javascript:;' onclick='getUrl()'>查看权限路径</a>";
            }
        }
    ]);

    // create the grid
    var grid = new Ext.grid.GridPanel({
        id:'clientUser',
        store: serverstore,
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
        iconCls:'icon-grid'
    });



    var panel = new Ext.Panel({
        frame:true,
        border:false,
        width:500,
        autoScroll:true,
        items:[{
            xtype:'fieldset',
            title:'认证账号',
            items:[form_panel]
        },{
            xtype:'fieldset',
            title:'可访问服务',
            items:[grid]
        }]
    });

    new Ext.Viewport({
        layout :'fit',
        renderTo:Ext.getBody(),
        items:[panel]
    });


    store.load();
    store.on('load',function(){
        var clientUser = store.getAt(0).get('clientUser');
        Ext.getCmp('client.clientUser.info').setValue(clientUser);
        var clientPwd = store.getAt(0).get('clientPwd');
        Ext.getCmp('client.clientPwd.info').setValue(clientPwd);
        var serverIp = store.getAt(0).get('serverIp');
        Ext.getCmp('server.serverIp.info').setValue(serverIp);
        var serverPort = store.getAt(0).get('serverPort');
        Ext.getCmp('server.serverPort.info').setValue(serverPort);
        var clientIp = store.getAt(0).get('clientIp');
        Ext.getCmp('client.clientIp.info').setValue(clientIp);
        var clientPort = store.getAt(0).get('clientPort');
        Ext.getCmp('client.clientPort.info').setValue(clientPort);
        var connect = store.getAt(0).get('connect');
        if(connect == true){
            Ext.getCmp('disconnect').enable();
            Ext.getCmp('connect').disable();
            Ext.getCmp('saveproperty').disable();
            Ext.getCmp('server.serverIp.info').disable(true);
            Ext.getCmp('server.serverPort.info').disable(true);
            Ext.getCmp('client.clientIp.info').disable(true);
            Ext.getCmp('client.clientPort.info').disable(true);
            Ext.getCmp('client.clientUser.info').disable(true);
            Ext.getCmp('client.clientPwd.info').disable(true);
        }else{
            Ext.getCmp('disconnect').disable();
            Ext.getCmp('connect').enable();
            Ext.getCmp('saveproperty').enable();
        }
    });

    //检查是否离线
    var task = {
        run:function () {
            Ext.Ajax.request({
                url:'ClientAction_checkState.action',
                success:function (r,o) {
                    var respText = Ext.util.JSON.decode(r.responseText);
                    var msg = respText.msg;
                    if (msg.indexOf("true") > -1) {
//                        Ext.getCmp('client.state').setText('在线');
                        Ext.getCmp('client.state').setValue('<font color=green size="3">在线</font> ');
                    }else{
//                        Ext.getCmp('client.state').setText('离线');
                        Ext.getCmp('client.state').setValue('<font color=red size="3">离线</font> ');
//                        Ext.getCmp('client.state').body.update('<font color="red" size="3">离线</font> ');
                    }

                }
            });
        },
        interval:10000
    };
    Ext.TaskMgr.start(task);


});

function getPwd(){
    var grid = Ext.getCmp('clientUser');
    var store = grid.getStore();
    var selModel = grid.getSelectionModel();
    if(selModel.hasSelection()){
        var selections = selModel.getSelections();
        Ext.each(selections,function(item){
            var formPanel = new Ext.form.FormPanel({
                frame:true,
                width:800,
                autoScroll:true,
                baseCls : 'x-plain',
                labelWidth:120,
                labelAlign:'right',
                defaultWidth:300,
                layout:'form',
                border:false,
                defaults:{
                    width:200
                },
                items:[
                    new Ext.form.TextField({
                        fieldLabel: '请输入认证密码',
//                                        minLength: 6,
//                                        maxLength: 30,
                        name:'pwd',
                        inputType: 'password',    //密码
                        allowBlank: false,
                        blankText:'请输入认证密码'
                    })
                ]
            });
            var select_Win = new Ext.Window({
                width:420,
                layout:'fit',
                height:100,
                modal:true,
                items:formPanel ,
                bbar:[ '->',{
                    id:'showPwd.info',
                    text:'确定',
                    width:50 ,
                    handler:function(){
                        if (formPanel.form.isValid()) {
                            formPanel.getForm().submit({
                                url: 'ClientAction_getPwd.action',
                                params:{serverId:item.data.serverId},
                                method: 'POST',
                                success : function(form, action) {
                                    var msg = action.result.msg;
                                    Ext.Msg.alert("提示密码", "服务密码为: "+msg);
                                    select_Win.close();
                                    grid.getStore().reload();
                                },
                                failure : function(result,action) {
                                    var msg = action.result.msg;
                                    Ext.Msg.alert("提示", msg);
                                }
                            });
                        }
                    }
                }]
            }).show();
        });
    }

}

function getUrl(){
    var grid = Ext.getCmp('clientUser');
    var store = grid.getStore();
    var selModel = grid.getSelectionModel();
    if(selModel.hasSelection()){
        var selections = selModel.getSelections();
        Ext.each(selections,function(item){
            Ext.Ajax.request({
                url:'ClientAction_getUrl.action',
                params:{serverName:item.data.serverUser},
                success:function (r,o) {
                    var respText = Ext.util.JSON.decode(r.responseText);
                    var msg = respText.msg;
                    Ext.Msg.alert("权限路径", msg);

                }
            });
            /*var formPanel = new Ext.form.FormPanel({
                frame:true,
                width:800,
                autoScroll:true,
                baseCls : 'x-plain',
                labelWidth:120,
                labelAlign:'right',
                defaultWidth:300,
                layout:'form',
                border:false,
                defaults:{
                    width:200
                },
                items:[
                    new Ext.form.TextField({
                        fieldLabel: '请输入认证密码',
//                                        minLength: 6,
//                                        maxLength: 30,
                        name:'pwd',
                        inputType: 'password',    //密码
                        allowBlank: false,
                        blankText:'请输入认证密码'
                    })
                ]
            });
            var select_Win = new Ext.Window({
                width:420,
                layout:'fit',
                height:100,
                modal:true,
                items:formPanel ,
                bbar:[ '->',{
                    id:'showPwd.info',
                    text:'确定',
                    width:50 ,
                    handler:function(){
                        if (formPanel.form.isValid()) {
                            formPanel.getForm().submit({
                                url: 'ClientAction_getUrl.action',
                                params:{serverId:item.data.serverId},
                                method: 'POST',
                                success : function(form, action) {
                                    var msg = action.result.msg;
                                    Ext.Msg.alert("提示权限路径", msg);
                                    select_Win.close();
                                    grid.getStore().reload();
                                },
                                failure : function(result,action) {
                                    var msg = action.result.msg;
                                    Ext.Msg.alert("提示", msg);
                                }
                            });
                        }
                    }
                }]
            }).show();*/
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
    var str = "";
    if(v.indexOf("1") > -1){
        str += "读;";
    }
    if(v.indexOf("2") > -1){
        str += "写;";
    }
    if(v.indexOf("3") > -1){
        str += "追加;";
    }
    if(v.indexOf("4") > -1){
        str += "重命名;";
    }
    if(v.indexOf("5") > -1){
        str += "删除;";
    }
    return str;
}

