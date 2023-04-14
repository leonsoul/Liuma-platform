/**
 * 用例中心  接口用例
 */
<template>
  <div>
    <page-header title="用例编辑" :showDebug="true" :cancel="cancelAdd" :debug="debugCase" :save="saveAdd"/>
    <el-form ref="caseForm" :rules="rules" :model="caseForm" label-width="90px">
        <base-info :caseForm="caseForm" :applications=[] v-on:getUseFunction="getUseFunction"/>
    <p class="tip">接口请求</p>
    <el-form-item style="margin-left:-80px;" prop="caseApis"/>
    <el-table :data="caseForm.caseApis" row-key="id" class="sort-table" size="small">
        <el-table-column label="" width="60px">
            <template>
                <i class="iconfont icon-paixu" @mousedown="rowDrop" style="font-size: 24px"/>
            </template>
        </el-table-column>
        <el-table-column label="序号" prop="index" width="100px">
        </el-table-column>
        <el-table-column label="接口名称" prop="apiName">
        </el-table-column>
        <el-table-column label="请求方式" prop="apiMethod">
        </el-table-column>
        <el-table-column label="接口地址" prop="apiPath">
        </el-table-column>
        <el-table-column label="步骤描述" min-width="200px">
            <template slot-scope="scope">
                <el-input size="mini" style="width: 90%" v-model="scope.row.description" placeholder="请输入步骤描述"/>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="200px">
            <template slot-scope="scope">
                <el-button size="mini" type="text" @click="editCaseApi(scope.$index)">编辑</el-button>
                <el-button size="mini" type="text" @click="copyCaseApi(scope.$index)">复制</el-button>
                <el-button size="mini" type="text" @click="deleteCaseApi(scope.$index)">删除</el-button>
                <el-button v-if="caseForm.caseApis[scope.$index].isBan" size="mini" type="text" @click="banCaseApi(scope.$index)">启用</el-button>
                <el-button v-else size="mini" type="text" @click="banCaseApi(scope.$index)">禁用</el-button>
            </template>
        </el-table-column>
    </el-table>
    </el-form>
    <el-button size="small" icon="el-icon-plus" type="text" @click="selectApiVisible=true">新增</el-button>
    <!-- 添加接口界面 -->
    <el-dialog title="选择接口" :visible.sync="selectApiVisible" width="800px" destroy-on-close>
        <select-api :selections="selections" :selectApiVisible="selectApiVisible"/>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="selectApiVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="selectApiSave">保存</el-button>
        </div>
    </el-dialog>
    <!-- 接口编辑界面 -->
    <el-drawer title="接口详情" :visible.sync="editCaseApiVisible"  direction="rtl" :with-header="false" destroy-on-close  :closeOnPressEscape="false" :wrapperClosable="false" size="900px">
        <div class="api-drawer-header">
            <span style="float: left; font-size: 16px;">接口详情编辑</span>
            <div style="float: right;">
              <el-button size="small"  @click="confirmApiCancel">取消</el-button>
              <el-button size="small" type="primary"  @click="confirmApiSave">确定</el-button>
            </div>
        </div>
        <div class="api-drawer-body">
            <el-tabs style="width: 100%" v-model="activeTab">
                <el-tab-pane label="请求头" name="header">
                    <request-header :reqHeader="caseApiForm.header" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="请求体" name="body">
                    <request-body :caseForm="caseForm" :reqBody="caseApiForm.body"   style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="QUERY参数" name="query">
                    <request-query :reqQuery="caseApiForm.query" :reqBody="caseApiForm.body" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="REST参数" name="rest">
                    <request-rest :reqRest="caseApiForm.rest" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="响应断言" name="assertion">
                    <assertion :assertion="caseApiForm.assertion" :apiId="caseApiForm.apiId" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="关联取值" name="relation">
                    <relation :relation="caseApiForm.relation" :apiId="caseApiForm.apiId" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="逻辑控件" name="controller">
                    <controller :controller="caseApiForm.controller" @editCodeCharge="editCodeCharge" style="width: 100%"/>
                </el-tab-pane>
            </el-tabs>
        </div>
    </el-drawer>
    <!-- 用例调试选择引擎和环境 -->
    <run-form :runForm="runForm" :runVisible="runVisible" :showEnvironment="true" @closeRun="closeRun" @run="run($event)"/>
    <!-- 用例执行结果展示 -->
    <run-result :taskId="taskId" :caseType="caseForm.type" :resultVisable="resultVisable" @closeResult="closeResult"/>
  </div>
</template>

<script>
import Sortable from 'sortablejs'
import {getUUID} from '@/utils/util'
import PageHeader from '../common/components/pageheader'
import BaseInfo from './common/case/baseInfo'
import SelectApi from './common/case/selectApi'
import RequestHeader from './common/api/requestHeader'
import RequestQuery from './common/api/requestQuery'
import RequestRest from './common/api/requestRest'
import RequestBody from './common/api/requestBody'
import Assertion from './common/case/assertion'
import Relation from './common/case/relation'
import Controller from './common/case/controller'
import RunForm from '@/views/common/business/runForm'
import RunResult from './common/case/runResult'

export default {
    components:{PageHeader, BaseInfo, SelectApi, RequestHeader, RequestQuery,
    RequestRest,RequestBody, Assertion, Relation, Controller, RunForm, RunResult},
    data() {
        return{
            caseForm: {
                id: "",
                name: "",
                level: "P0",
                type: "API",
                environmentIds: [],
                thirdParty: "",
                moduleId: "",
                moduleName: "",
                commonParam: {
                    functions: [],
                    params: [],
                    header: "",
                    proxy: ""
                },
                caseApis: []
            },
            selections:[],
            editCaseApiVisible: false,
            selectApiVisible: false,
            runVisible: false,
            caseApiForm:{
                header: [],
                body: { type: 'json',form: [],json: '',raw: '',file: []},
                rest: [],
                query: [],
                assertion: [],
                relation: [],
                controller: []
            },
            caseApiFormCopy:{},
            caseApiIndexCopy: 0,
            activeTab: "body",
            runForm: {
                engineId: "",
                environmentId: null,
                deviceId: null
            },
            resultVisable: false,
            taskId: "",
            rules: {
                name: [{ required: true, message: '用例名称不能为空', trigger: 'blur' }],
                type: [{ required: true, message: '用例类型不能为空', trigger: 'blur' }],
                moduleId: [{ required: true, message: '用例模块不能为空', trigger: 'blur' }],
                caseApis: [{ required: true, message: '请至少添加一条接口请求', trigger: 'blur' }]
            },
            FunctionList: [],
            isShow: true,
            isCodeEdit: false
        }
    },
    created() {
        // 配置顶部跳转面包屑，跳转路由的话需要修改这边和navcon中的结构。如[{'name':'用例中心','path':'/home/dashboard'}]
        this.$root.Bus.$emit('initBread', ["用例中心", "接口用例"]);
        this.getDetail(this.$route.params);
    },
    methods: {
        // 行拖拽
        rowDrop () {
            // 此时找到的元素是要拖拽元素的父容器
            const tbody = document.querySelector('.sort-table tbody');
            const _this = this;
            Sortable.create(tbody, {
                //  指定父元素下可被拖拽的子元素
                draggable: ".el-table__row",
                onEnd ({ newIndex, oldIndex }) {
                    const currRow = _this.caseForm.caseApis.splice(oldIndex, 1)[0];
                    _this.caseForm.caseApis.splice(newIndex, 0, currRow);
                    _this.sortCaseApi();
                }
            });
        },
        // 重新排序
        sortCaseApi(){
            for(let i=0; i<this.caseForm.caseApis.length; i++){
                this.caseForm.caseApis[i].index = i+1;
            }
        },
        // 保存接口选择
        selectApiSave(){
            for(let i=0;i<this.selections.length;i++){
                let caseApi = {
                    id: getUUID(),
                    index: this.caseForm.caseApis.length+1,
                    apiId: this.selections[i].id,
                    apiMethod: this.selections[i].method,
                    apiName: this.selections[i].name,
                    apiPath: this.selections[i].path,
                    description: this.selections[i].description
                }
                this.caseForm.caseApis.push(caseApi);
            }
            this.selections.splice(0, this.selections.length);
            this.selectApiVisible = false;
        },
        // 保存接口选择
        confirmApiSave(){

            if(this.isCodeEdit === true){
                this.isCodeEdit = false;
                this.$message.warning('正在进行编辑，再次点击确定能够进行保存，不会保存当前代码数据');

            }
            else{
                this.editCaseApiVisible=false
                // this.$message.success('我关闭了');
            }
        },
        editCaseApi(index){
            let caseApi = this.caseForm.caseApis[index];
            this.activeTab = "body";
            if(!caseApi.body){
                let url = "/autotest/api/detail/" + caseApi.apiId;
                this.$get(url, response =>{
                    let data = response.data;
                    if(data.header){
                        data.header = JSON.parse(data.header);
                    }
                    if(data.body){
                        data.body = JSON.parse(data.body);
                    }
                    if(data.query){
                        data.query = JSON.parse(data.query);
                    }
                    if(data.rest){
                        data.rest = JSON.parse(data.rest);
                    }
                    caseApi.header = data.header;
                    caseApi.body = data.body;
                    caseApi.rest = data.rest;
                    caseApi.query = data.query;
                    caseApi.assertion = [];
                    caseApi.relation = [];
                    caseApi.controller = [];
                    this.caseApiForm = caseApi;
                    this.editCaseApiVisible = true;
                });
            }else{
                this.caseApiForm = caseApi;
                this.editCaseApiVisible = true;
            }
            this.caseApiIndexCopy = index
            this.caseApiFormCopy = JSON.parse(JSON.stringify(this.caseApiForm))
        },
        confirmApiCancel(){
            this.caseForm.caseApis[this.caseApiIndexCopy] = JSON.parse(JSON.stringify(this.caseApiFormCopy));
            this.editCaseApiVisible = false;
        },
        deleteCaseApi(index){
            this.caseForm.caseApis.splice(index, 1);
            for(let i=0; i<this.caseForm.caseApis.length; i++){
                this.caseForm.caseApis[i].index = i+1;
            }
        },
        copyCaseApi(index){
            // 拷贝复制的用例的所有数据
            let caseApi = JSON.parse(JSON.stringify(this.caseForm.caseApis[index]))
            caseApi.id = getUUID();
            caseApi.index = this.caseForm.caseApis.length+1;
            // console.log(this.caseForm.caseApis);
            this.caseForm.caseApis.push(caseApi);
            this.selections.splice(0, this.selections.length);
        },
        banCaseApi(index){
            if(this.caseForm.caseApis[index].isBan === true){
              this.$set( this.caseForm.caseApis[index],'isBan',false);
            }
            else{
              this.$set( this.caseForm.caseApis[index],'isBan',true);
            }
        },
        getDetail(param){
            if (param.caseId){  // 编辑
                let url = "/autotest/case/detail/api/" + param.caseId;
                this.$get(url, response => {
                    let data = response.data;
                    if(data.environmentIds){
                        data.environmentIds = JSON.parse(data.environmentIds);
                    }
                    if(data.commonParam){
                        data.commonParam = JSON.parse(data.commonParam);
                    }
                    for(let i=0;i<data.caseApis.length;i++){
                        let caseApi = data.caseApis[i];
                        if(caseApi.header){
                            caseApi.header = JSON.parse(caseApi.header);
                        }
                        if(caseApi.body){
                            caseApi.body = JSON.parse(caseApi.body);
                        }
                        if(caseApi.query){
                            caseApi.query = JSON.parse(caseApi.query);
                        }
                        if(caseApi.rest){
                            caseApi.rest = JSON.parse(caseApi.rest);
                        }
                        if(caseApi.assertion){
                            caseApi.assertion = JSON.parse(caseApi.assertion);
                        }
                        if(caseApi.relation){
                            caseApi.relation = JSON.parse(caseApi.relation);
                        }
                        if(caseApi.controller){
                            caseApi.controller = JSON.parse(caseApi.controller);
                        }
                    }
                    if(param.type === "copy"){ //复用
                        data.id = "";
                    }
                    this.caseForm = data;
                    console.log(this.caseForm.caseApis)
                });
            }
        },
        cancelAdd(){
            this.$router.push({path: '/caseCenter/caseManage'})
        },
        saveAdd(){
            this.$refs["caseForm"].validate(valid => {
                if (valid) {
                    this.caseForm.projectId = this.$store.state.projectId;
                    for(let i=0; i<this.caseForm.caseApis.length; i++){
                        this.caseForm.caseApis[i].index = i+1;
                    }
                    let url = '/autotest/case/save';
                    console.log(this.caseForm)
                    this.$post(url, this.caseForm, response =>{
                        this.$message.success("保存成功");
                        // 如果是复用过来的用例的话，点击保存返回到用例列表页面，如果是编辑、新增的话，仍然停留在编辑页。
                        if(this.$route.params.type === 'copy'){
                          this.$router.push({path: '/caseCenter/caseManage'});
                        }
                    });
                }else{
                    return false;
                }
            });
        },
        debugCase(){
            // 用例调试
            this.runForm.engineId = 'system';
            if(this.caseForm.environmentIds.length > 0){
                this.runForm.environmentId = this.caseForm.environmentIds[0];
            }
            this.runForm.sourceType = "temp";
            this.runForm.sourceId = this.caseForm.id;
            this.runForm.sourceName = this.caseForm.name;
            this.runForm.taskType = "debug";
            this.runForm.projectId = this.$store.state.projectId;
            this.runForm.debugData = JSON.parse(JSON.stringify(this.caseForm));

            for(let i=0 ; i<this.runForm.debugData.caseApis.length; i++){
                if(this.runForm.debugData.caseApis[i].isBan === true){
                  // console.log(this.runForm.debugData.caseApis[i]);
                  this.runForm.debugData.caseApis.splice(i,1);
                  i--;
              }
            }
            // console.log(this.runForm.debugData.caseApis);
            this.runVisible = true;
        },
        closeRun(){
            this.runVisible = false;
        },
        run(runForm){
            let url = '/autotest/run';
            this.$post(url, runForm, response =>{
                this.taskId = response.data.id;
                this.resultVisable = true;
            });
            this.runVisible = false;
        },
        closeResult(){
            this.resultVisable = false;
        },
        getUseFunction(data){
          console.log(data)
          this.FunctionList = data
        },
        editCodeCharge(flag){
            if(flag==true){
                this.isCodeEdit = true;
            }else{
                this.isCodeEdit = false;
            }
        }
    }
}
</script>

<style scoped>
.api-drawer-header{
    border-bottom: 1px solid rgb(219, 219, 219);
    height: 60px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0px 20px;
}
.api-drawer-body{
    padding: 10px 20px;
}
</style>
