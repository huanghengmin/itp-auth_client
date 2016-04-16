/**
 * 恢复出厂密码设置
 */
Ext.onReady(function(){
    Ext.BLANK_IMAGE_URL = '../../js/ext/resources/images/default/s.gif';
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';

    var initForm = new Ext.form.FormPanel({
        plain:true,
        border:false,
        loadMask : { msg : '正在加载数据,请稍后.....' },
        labelAlign:'right',
        labelWidth:150,
        defaultType:'displayfield',
        defaults:{
            width:200,
            allowBlank:false,
            blankText:'该项不能为空!'
        },
        items:[{
        	fieldLabel:"密码重置",
        	html:"<a href='javascript:;' onclick='init_pwd();' style='color: green;'>重置</a>"
        },{
            id:'checkNumber.info',
            fieldLabel:'校验码',
            xtype:'textfield',
            name:'checkNumber'
        }]
    });

    var uKeyForm = new Ext.form.FormPanel({
        plain:true,
        border:false,
        loadMask : { msg : '正在加载数据,请稍后.....' },
        labelAlign:'right',
        labelWidth:150,
        defaultType:'displayfield',
        defaults:{
            width:200,
            allowBlank:false,
            blankText:'该项不能为空!'
        },
        items:[{
            fieldLabel:"许可证上传",
            html:"<a href='javascript:;' onclick='internal_refresh_win();' style='color: green;'>上传许可证</a>"
        },{
        	id:'product.info', xtype:'displayfield', fieldLabel : '产品标识'
        },{
        	id:'center.info', xtype:'displayfield', fieldLabel : '内核'
        },{
        	id:'os.info', xtype:'displayfield', fieldLabel : '操作系统'
        },{
            id:'maxConnect.info', xtype:'displayfield', fieldLabel : '视频接入路数'
        },{
            id:'maxBandwidth.info', xtype:'displayfield', fieldLabel : '并发带宽'
        },{
            id:'internal.uKeyId.info', xtype:'displayfield', fieldLabel:'UKey编号'
        },{
            id:'internal.licenseId.info', xtype:'displayfield', fieldLabel:'许可证编号'
        },{
            id:'internal.startDate.info',  xtype:'displayfield', fieldLabel:'开始时间'
        },{
            id:'internal.endDate.info', xtype:'displayfield', fieldLabel:'结束时间'
        },{
            id:'internal.licenseDays.info', xtype:'displayfield', fieldLabel:'许可天数'
        },{
            id:'internal.m.info', xtype:'displayfield', fieldLabel:'模块'
        }]
    });

    var panel = new Ext.Panel({
        plain:true,
        width:setWidth(),
        border:false,
        autoScroll:true,
        items:[{
            xtype:'fieldset',
            title:'恢复出厂设置',
//            width:500,
            items:[initForm]
        },{
            xtype:'fieldset',
            title:'UKey项查询',
//            width:500,
            items:[uKeyForm]
        }]
    });

    new Ext.Viewport({
    	layout:'fit',
    	renderTo:Ext.getBody(),
    	items:[{
            frame:true,
            autoScroll:true,
            items:[panel]
        }]
    });
    var record = new Ext.data.Record.create([
        {name:'product',mapping:'product'},
        {name:'center',mapping:'center'},
        {name:'os',mapping:'os'},
        {name:'maxConnect',mapping:'maxConnect'},
        {name:'maxBandwidth',mapping:'maxBandwidth'},
        {name:'uKeyId',mapping:'uKeyId'},
        {name:'licenseId',mapping:'licenseId'},
        {name:'startDate',mapping:'startDate'},
        {name:'endDate',mapping:'endDate'},
        {name:'licenseDays',mapping:'licenseDays'},
        {name:'m',mapping:'m'}
	]);
	var proxy = new Ext.data.HttpProxy({
		url:"UKeyQueryAction_queryUKey.action"
	});
	var reader = new Ext.data.JsonReader({
		totalProperty:"total",
		root:"rows"
	},record);
	var store = new Ext.data.Store({
		proxy : proxy,
		reader : reader
	});
	store.load();
	store.on('load',function(){
		var product = store.getAt(0).get('product');
		var center = store.getAt(0).get('center');
		var os = store.getAt(0).get('os');
		var maxConnect = store.getAt(0).get('maxConnect');
		var maxBandwidth = store.getAt(0).get('maxBandwidth');
        var uKeyId = store.getAt(0).get('uKeyId');
		var licenseId = store.getAt(0).get('licenseId');
		var startDate = store.getAt(0).get('startDate');
        var endDate = store.getAt(0).get('endDate');
        var licenseDays = store.getAt(0).get('licenseDays');
        var m = store.getAt(0).get('m');
        Ext.getCmp('internal.uKeyId.info').setValue(uKeyId);
        Ext.getCmp('internal.licenseId.info').setValue(licenseId);
        Ext.getCmp('internal.startDate.info').setValue(startDate);
        Ext.getCmp('internal.endDate.info').setValue(endDate);
        Ext.getCmp('internal.licenseDays.info').setValue(licenseDays);
        Ext.getCmp('internal.m.info').setValue(m);
        if(uKeyId!=licenseId){
        	Ext.MessageBox.show({
                title:'信息',
                msg:'uKey编号和许可证编号不同!',
                width:250,
                buttons:{'ok':'确定'},
                icon:Ext.MessageBox.INFO,
                closable:false
            });
        }
		Ext.getCmp('product.info').setValue(product);
		Ext.getCmp('center.info').setValue(center);
		Ext.getCmp('os.info').setValue(os);
        if(maxConnect.length==0){
            Ext.getCmp('maxConnect.info').destroy();
            Ext.getCmp('maxBandwidth.info').destroy();
        } else {
            Ext.getCmp('maxConnect.info').setValue(maxConnect);
            Ext.getCmp('maxBandwidth.info').setValue(maxBandwidth);
        }
	});
});

function setWidth(){
    return document.body.clientWidth-15;
}

/**
 * 初始化数据库
 */
function init_pwd() {
    Ext.MessageBox.show({
        title:'信息',
        width:200,
        msg:'确定要重置密码?',
        animEl:'init.info',
        buttons:{'ok':'确定','no':'取消'},
        icon:Ext.MessageBox.QUESTION,
        closable:false,
        fn:function(e){
            if(e=='ok'){
                var myMask = new Ext.LoadMask(Ext.getBody(), {
					msg: '正在重置,请稍后...',
					removeMask: true //完成后移除
				});
				myMask.show();
                var checkNumber = Ext.getCmp('checkNumber.info').getValue();
                if(checkNumber.length>0){
                    Ext.Ajax.request({
                        url : 'ChangePasswordAction_init.action',
                        method:'POST',
                        params :{checkNumber:checkNumber},
                        success : function(response, option) {
                            var result = Ext.util.JSON.decode(response.responseText);
                            myMask.hide();
                            Ext.MessageBox.show({
                                title:'信息',
                                msg:result.msg+',按[确定]返回',
                                buttons:{'ok':'确定'},
                                icon:Ext.MessageBox.INFO,
                                closable:false
                            });
                        }
                    });
                } else {
                    myMask.hide();
                    Ext.MessageBox.show({
                        title:'信息',
                        msg:'校验码不能为空,按[确定]返回',
                        buttons:{'ok':'确定'},
                        icon:Ext.MessageBox.ERROR,
                        closable:false
                    });
                }
            }
        }
    });
}

function internal_refresh_win(){
    var internalForm = new Ext.form.FormPanel({
        plain:true,
        labelWidth:100,
        labelAlign:'left',
        fileUpload:true,
        border:false,
        defaults : {
            width : 200,
            allowBlank : false,
            blankText : '该项不能为空！'
        },
        items:[{
            id:'internalLicense.info',
            fieldLabel:"许可证上传",
            name:'uploadFile',
            xtype:'textfield',
            inputType: 'file'
        }]
    });
    var win = new Ext.Window({
        title:'本地许可证更新',
        modal:true,
        width:400,
        height:300,
        layout:'fit',
        items:[{
        	frame:true,
        	items:[{height:20},{
	        	height:100,
	        	items:[{
	        		xtype:'fieldset',
	        		title:'说明',
	        		html:"<font color='green'>上传的文件名必须是 “license”!</font>"
	        	}]
	        },internalForm
	        ]
        }],
        bbar:['->',{
            text:'上传',
            id:'uploading.internal.info',
            handler: function() {
                if(internalForm.form.isValid()){
//                	Ext.MessageBox.show({
//                        title: '请等待',
//                        msg: '上传中...',
//                        progressText: '',
//                        width:300,
//                        progress:true,
//                        closable:true,
//                        animEl:'uploading.internal.info'
//                    });
                    internalForm.getForm().submit({
                        url:'UKeyQueryAction_uploadLicense.action',
                        method:'POST',
                        success: function(form,action) {
                            var flag = action.result.msg;
//                            Ext.MessageBox.hide();
//                            alert(flag);
                            Ext.MessageBox.show({
                                title:'信息',
                                msg:flag,
                                width:210,
                                animEl:'uploading.internal.info',
                                buttons:{'ok':'确定'},
                                icon:Ext.MessageBox.INFO,
                                closable:false,
                                fn:function(e){
                                	if(e=='ok'){
                                		win.close();
                                	}
                                }
                            });
//                            Ext.getCmp('internalLicense.info').setValue('');
                        },
                        failure: function(){
                            Ext.Msg.show({
                                title:'信息',
                                msg:'许可证上传失败！',
                                width:210,
                                animEl:'uploading.internal.info',
                                buttons:{'ok':'确定'},
                                icon:Ext.MessageBox.ERROR,
                                closable:false
                            });
                            Ext.getCmp('internalLicense.info').setValue('');
                        }
                    });
                }else{
                    Ext.Msg.show({
                        title:'信息',
                        msg:'请选择需要上传的许可证！',
                        width:230,
                        animEl:'uploading.internal.info',
                        buttons:{'ok':'确定'},
                        icon:Ext.MessageBox.ERROR,
                        closable:false
                    });
                }
            }
        },{
            text:'关闭',
            handler:function(){
                win.close();
            }
        }]
    }).show();
}