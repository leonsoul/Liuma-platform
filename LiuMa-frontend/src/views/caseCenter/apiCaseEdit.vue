/**
 * 用例中心  接口用例
 */
<template>
  <div>
    <page-header title="编辑用例" :showDebug="true" :cancel="cancelAdd" :debug="debugCase" :save="saveAdd"/>
    <el-form ref="caseForm" :rules="rules" :model="caseForm" label-width="90px">
    <base-info :caseForm="caseForm" :applications=[] v-on:getUseFunction="getUseFunction" v-on:callbackUseFunctionDetail="callbackUseFunctionDetail" v-on:callbackUseParamDetail="callbackUseParamDetail"/>
    <p class="tip">接口请求
      <el-button v-if="taskId" style="float:right;"  @click="resultVisable=true ">查看当前调试请求记录</el-button>
    </p>
    <el-form-item style="margin-left:-80px;" prop="caseApis"/>
    <el-table :data="caseForm.caseApis" row-key="id" class="sort-table" size="small">
        <el-table-column label="" width="60px">
            <template>
                <i class="iconfont icon-paixu" style="font-size: 24px"/>
            </template>
        </el-table-column>
        <el-table-column label="序号" prop="index" width="100px">
        </el-table-column>
        <el-table-column label="接口名称" prop="apiName">
        </el-table-column>
      <el-table-column min-width="90px">
        <template v-slot="scope">
            <el-tag v-if="scope.row.relation && scope.row.relation.length" class="font-size-12">关联
              <span style="font-size: 10px;margin-left: -2px;">({{scope.row.relation.length}})</span>
            </el-tag>
            <el-tag v-if="scope.row.assertion && scope.row.assertion.length" color="#c6f5d7" class="font-size-12">断言
              <span style="font-size: 10px;margin-left: -2px;">({{scope.row.assertion.length}})</span>
            </el-tag>
        </template>
      </el-table-column>
        <el-table-column label="请求方式" prop="apiMethod">
        </el-table-column>
        <el-table-column label="接口地址" prop="apiPath">
        </el-table-column>
        <el-table-column label="步骤描述" min-width="200px">
            <template v-slot="scope">
                <div v-if="scope.row.edit===true" >
                  <el-input size="mini" style="width: 85%" v-model="scope.row.description" placeholder="请输入步骤描述" @change="scope.row.edit=false"/>
                  <i class="el-icon-success" @click="scope.row.edit=false"/>
                </div>
              <span v-else>{{scope.row.description}} <i class="el-icon-edit" @click="changeInDesc(scope.row)"/></span>
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
    <el-button size="small" icon="el-icon-plus" type="text" @click="selectApiVisible=true">选择接口</el-button>
    <el-button size="small" icon="el-icon-plus" type="text" @click="addApi">新增接口</el-button>
    <!-- 添加接口界面 -->
    <el-dialog title="选择接口" :visible.sync="selectApiVisible" width="800px" destroy-on-close>
        <select-api :selections="selections" :selectApiVisible="selectApiVisible"/>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="selectApiVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="selectApiSave">保存</el-button>
        </div>
    </el-dialog>
    <!-- 接口编辑界面 -->
    <el-drawer title="接口详情" :visible.sync="editCaseApiVisible" direction="rtl" :with-header="false"
               destroy-on-close  :closeOnPressEscape="false" size="920px" :wrapperClosable="false">
        <div class="api-drawer-header">
            <span style="float: left; font-size: 16px;">接口详情编辑</span>
            <div style="float: right;">
              <el-button size="small"  @click="confirmApiCancel">取消</el-button>
              <el-button v-if="isAddApi" size="small" type="primary" style="float: right;" @click="saveCaseApi">保存</el-button>
              <el-button v-else size="small" type="primary"  @click="saveCaseApi">确定</el-button>
            </div>
        </div>
        <div class="api-drawer-body">
            <el-form v-if="isAddApi" ref="caseApiForm" :rules="apiRules" :model="caseApiForm" label-width="80px">
                <el-row :gutter="20">
                    <el-col :span="16">
                        <el-form-item label="接口请求" prop="path">
                            <el-input size="small" v-model="caseApiForm.path" placeholder="请输入接口地址" style="margin-top: 5px">
                                <el-select v-model="caseApiForm.method" slot="prepend" style="width: 80px" size="small">
                                    <el-option v-for="item in methods" :key="item" :label="item" :value="item"/>
                                </el-select>
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="请求协议" prop="protocol">
                            <el-select size="small" style="width:100%" v-model="caseApiForm.protocol" placeholder="请选择请求协议">
                                <el-option v-for="item in protocols" :key="item" :label="item" :value="item"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="16">
                        <el-form-item label="接口名称" prop="name">
                            <el-input  size="small" v-model="caseApiForm.name" placeholder="请输入接口名称"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="域名标识" prop="domainSign">
                            <el-select style="width:100%" size="small" v-model="caseApiForm.domainSign" clearable placeholder="请选择域名标识">
                                <el-option v-for="item in domains" :key="item.id" :label="item.name" :value="item.id"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="16">
                        <el-form-item label="接口描述">
                            <el-input size="small" clearable placeholder="请输入接口描述" v-model="caseApiForm.description"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="模块分类" prop="moduleId">
                            <select-tree style="width:100%" placeholder="请选择模块分类" :selectedValue="caseApiForm.moduleId"
                                :selectedLabel="caseApiForm.moduleName" :treeData="modules" @selectModule="selectModule($event)"/>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <el-input v-else size="small" disabled v-model="caseApiForm.apiPath" placeholder="请输入接口地址" style="margin-top: 5px">
                <el-select v-model="caseApiForm.apiMethod" slot="prepend" style="width: 80px" size="small">
                    <el-option v-for="item in methods" :key="item" :label="item" :value="item"/>
                </el-select>
            </el-input>
            <el-tabs style="width: 100%" v-model="activeTab">
                <el-tab-pane label="请求头" name="header">
                    <request-header :reqHeader="caseApiForm.header" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="请求体" name="body">
                    <request-body :caseForm="caseForm" :reqBody="caseApiForm.body" :supplementationList="supplementationList"  style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="查询参数" name="query">
                    <request-query :reqQuery="caseApiForm.query" :reqBody="caseApiForm.body" :supplementationList="supplementationList" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="路径参数" name="rest">
                    <request-rest :reqRest="caseApiForm.rest" :supplementationList="supplementationList" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="断言" name="assertion">
                    <assertion :assertion="caseApiForm.assertion" :apiId="caseApiForm.apiId" :supplementationList="supplementationList" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="关联" name="relation">
                    <relation :relation="caseApiForm.relation" :apiId="caseApiForm.apiId" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="前置脚本" name="pres">
                    <pre-script :pres="caseApiForm.pres" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="后置脚本" name="posts">
                    <post-script :posts="caseApiForm.posts" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="请求设置" name="settings">
                    <api-setting :settings="caseApiForm.settings" style="width: 100%"/>
                </el-tab-pane>
                <el-tab-pane label="逻辑控制" name="logics">
                    <logic-control :logics="caseApiForm.logics" style="width: 100%"/>
                </el-tab-pane>
<!--                <el-tab-pane label="逻辑控件" name="controller">-->
<!--                    <controller :controller="caseApiForm.controller" :functionListDetail="functionListDetail" @editCodeCharge="editCodeCharge" style="width: 100%"/>-->
<!--                </el-tab-pane>-->
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
// import Controller from './common/case/controller'
import LogicControl from './common/case/logicControl'
import PreScript from './common/case/preScript'
import PostScript from './common/case/postScript'
import ApiSetting from './common/case/apiSetting'
import RunForm from '@/views/common/business/runForm'
import RunResult from './common/case/runResult'
import SelectTree from '@/views/common/business/selectTree'

export default {
    components:{PageHeader, BaseInfo, SelectApi, RequestHeader, RequestQuery, SelectTree,
    RequestRest,RequestBody, Assertion, Relation, LogicControl, RunForm, RunResult,
    PreScript, PostScript, ApiSetting},
// ,Controller
    data() {
        return{
            caseForm: {
                id: "",
                name: "",
                level: "P0",
                type: "API",
                environmentIds: [],
                thirdParty: "",
                moduleId: "0",
                moduleName: "默认模块",
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
                controller: [],
                pres: [],
                posts: [],
                settings: [],
                logics: []
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
                caseApis: [{ required: true, message: '请至少添加一条接口请求', trigger: 'blur' }]
            },
            apiRules: {
                name: [{ required: true, message: '接口名称不能为空', trigger: 'blur' }],
                protocol: [{ required: true, message: '请求协议不能为空', trigger: 'blur' }],
                path: [{ required: true, message: '接口地址不能为空', trigger: 'blur' }]
            },
            domains: "",
            methods: ['POST', 'GET', 'PUT', 'DELETE', 'HEAD', 'PATCH', 'OPTIONS'],
            modules: [],
            protocols: ["HTTP"],
            isAddApi: false,
            FunctionList: [],
            supplementationList :[],
            isShow: true,
            isCodeEdit: false,
            functionListDetail: [],
            paramListDetail: [],
            debugEnvironmentId: "",
        }
    },
    created() {
        // 配置顶部跳转面包屑，跳转路由的话需要修改这边和navcon中的结构。如[{'name':'用例中心','path':'/home/dashboard'}]
        this.$root.Bus.$emit('initBread', ["用例中心", "接口用例"]);
        this.getDomain();
        this.getModule();
        this.getDetail(this.$route);
    },
    async mounted() {
      //行拖拽方法
      this.rowDrop();
    },
    watch:{
        "caseForm.commonParam.functions":{
          handler(){

            this.addSupplementationList('function')
          }
        },
        "functionListDetail":{
          handler(){

            this.addSupplementationList('function')
          }
        },
        "caseForm.commonParam.params":{
          handler(){

            this.addSupplementationList('param')
          }
        },
        "paramListDetail": {
            handler(){

              this.addSupplementationList('param')
          }
        }
    },
    methods: {

        // 行拖拽
        rowDrop () {
            // 此时找到的元素是要拖拽元素的父容器
            const tbody = document.querySelector('.sort-table tbody ');
            const _this = this;
            const sortable = Sortable.create(tbody, {
                draggable: '.el-table__row',
                //  指定父元素下可被拖拽的子元素
                onEnd ({ item, newIndex, oldIndex }) {
                    // item.draggable = false;
                    // sortable.option('disabled', true);
                    const currRow = _this.caseForm.caseApis.splice(oldIndex, 1)[0];
                    _this.caseForm.caseApis.splice(newIndex, 0, currRow);
                    _this.sortCaseApi();
                },
                onStart(){
                },
              handle: '.el-table__row td:nth-child(1)'


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
                    description: this.selections[i].description,
                }
                this.caseForm.caseApis.push(caseApi);
            }
            this.selections.splice(0, this.selections.length);
            this.selectApiVisible = false;
        },
        addApi(){
            this.isAddApi = true;
            this.activeTab = "body";
            this.caseApiForm = {
                id: "",
                name: "",
                level: "P1",
                path: "",
                method: "GET",
                protocol: "HTTP",
                domainSign: "",
                moduleId: "0",
                moduleName: "默认模块",
                description: "",
                header: [],
                body: {
                    type: 'json',
                    form: [],
                    json: '',
                    raw: '',
                    file: []
                },
                query: [],
                rest: [],
                assertion: [],
                relation: [],
                controller: [],
                pres: [],
                posts: [],
                settings: [],
                logics: []
            };
            this.editCaseApiVisible = true;
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
            this.isAddApi = false;
            this.activeTab = "body";
            if(!caseApi.body){
                // 如果body中没有值，使用默认值，请求接口获得接口信息
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
                    // 增加了三种控件配置
                    caseApi.pres = [];
                    caseApi.posts = [];
                    caseApi.settings = [];
                    caseApi.logics = [];
                    this.caseApiForm = caseApi;
                    this.editCaseApiVisible = true;
                });
            }else{

                let controller = JSON.parse(JSON.stringify(caseApi.controller));
                caseApi.pres = [];
                caseApi.posts = [];
                caseApi.settings = [];
                caseApi.logics = [];
                let preIndex = 1;
                let postIndex = 1;
                for(let i=0; i<controller.length; i++){
                    switch(controller[i].name) {
                        case 'preScript':
                            controller[i].index = preIndex;
                            controller[i].edit = false;
                            preIndex = preIndex+1;
                            caseApi.pres.push(controller[i]);
                            break;
                        case 'preSql':
                            controller[i].index = preIndex;
                            controller[i].edit = false;
                            preIndex = preIndex+1;
                            caseApi.pres.push(controller[i]);
                            break;
                        case 'postScript':
                            controller[i].index = postIndex;
                            controller[i].edit = false;
                            postIndex = postIndex+1;
                            caseApi.posts.push(controller[i]);
                            break;
                        case 'postSql':
                            controller[i].index = postIndex;
                            controller[i].edit = false;
                            postIndex = postIndex+1;
                            caseApi.posts.push(controller[i]);
                            break;
                        case 'whetherExec':
                            caseApi.logics.push(controller[i]);
                            break;
                        case 'loopExec':
                            caseApi.logics.push(controller[i]);
                            break;
                        default:
                            caseApi.settings.push(controller[i]);
                    }
                }
                this.caseApiForm = caseApi;
                this.editCaseApiVisible = true;
            }
            this.caseApiIndexCopy = index
            this.caseApiFormCopy = JSON.parse(JSON.stringify(caseApi))
        },
        confirmApiCancel(){
            // 取消编辑接口
            if(!this.isAddApi){
              this.$set( this.caseForm.caseApis,this.caseApiIndexCopy,JSON.parse(JSON.stringify(this.caseApiFormCopy)));
            }
            this.editCaseApiVisible = false;
        },
        saveCaseApi(){
          // 判断是否是新增接口
              if(this.isAddApi){
                  // 是新增接口
                  this.$refs["caseApiForm"].validate(valid => {
                      if (valid) {
                          this.caseApiForm.projectId = this.$store.state.projectId;
                          let url = '/autotest/api/save';
                          this.$post(url, this.caseApiForm, response =>{
                              this.$message.success("接口新增成功");
                              this.caseApiForm.controller = [];
                              this.caseApiForm.controller.push(...this.caseApiForm.logics);
                              this.caseApiForm.controller.push(...this.caseApiForm.pres);
                              this.caseApiForm.controller.push(...this.caseApiForm.posts);
                              this.caseApiForm.controller.push(...this.caseApiForm.settings);
                              let caseApi = {
                                  id: getUUID(),
                                  index: this.caseForm.caseApis.length+1,
                                  apiId: response.data,
                                  apiMethod: this.caseApiForm.method,
                                  apiName: this.caseApiForm.name,
                                  apiPath: this.caseApiForm.path,
                                  description: this.caseApiForm.description,
                                  header: this.caseApiForm.header,
                                  body: this.caseApiForm.body,
                                  query: this.caseApiForm.query,
                                  rest: this.caseApiForm.rest,
                                  assertion: this.caseApiForm.assertion,
                                  relation: this.caseApiForm.relation,
                                  controller: this.caseApiForm.controller,
                                  edit: false
                              };
                              this.caseForm.caseApis.push(caseApi);
                              this.editCaseApiVisible = false;
                          });
                      }else{
                          return false;
                      }
                  });
              }else{

                if(this.isCodeEdit === true){
                  this.isCodeEdit = false;
                  this.$message.warning('正在进行编辑，再次点击确定能够进行保存，不会保存当前代码数据');

                }
                // 将窗口关闭,在caseApiForm中加入接口中的控制信息
                this.caseApiForm.controller = [];
                this.caseApiForm.controller.push(...this.caseApiForm.logics);
                this.caseApiForm.controller.push(...this.caseApiForm.pres);
                this.caseApiForm.controller.push(...this.caseApiForm.posts);
                this.caseApiForm.controller.push(...this.caseApiForm.settings);
                this.editCaseApiVisible = false;
            }
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
            this.caseForm.caseApis.push(caseApi);
        },
        banCaseApi(index){
            // 禁用接口
            if(this.caseForm.caseApis[index].isBan === true){
              this.$set( this.caseForm.caseApis[index],'isBan',false);
            }
            else{
              this.$set( this.caseForm.caseApis[index],'isBan',true);
            }
        },
        addSupplementationList(type){
          // 增加输入时的辅助输入，主要是用于token的输入
          let _this = this;
          if(type === 'relation'){
            for(let item of _this.caseForm.caseApis){
              if(item.relation){
                for(let relationName of item.relation){  // 将接口存在的关联变量加入的候补输入中
                  let newRelationName = {'name':"{{"+ relationName.name+"}}"}
                  if(!_this.supplementationList.some(function (supplementation){
                    return supplementation.name === newRelationName.name
                  }))
                    _this.supplementationList.splice(_this.supplementationList.length, 0, newRelationName);
                }
              }
            }
          }
          else if(type==='param'){
            for(let commonParam of _this.caseForm.commonParam.params){
              for(let paramDetail of _this.paramListDetail){
                if(commonParam === paramDetail.id){  // 将添加的公参加入的候补输入中
                  let newParamName = {'name': "{{"+paramDetail.name+"}}"}
                  if(!_this.supplementationList.some(function (supplementation){
                    return supplementation.name === newParamName.name
                  })){ _this.supplementationList.splice(_this.supplementationList.length, 0, newParamName);}
                }
              }
            }
          }
          else if(type==='function'){
            for(let commonFunction of _this.caseForm.commonParam.functions){
              for(let functionDetail of _this.functionListDetail){
                if(commonFunction === functionDetail.id){  // 将添加的自定义函数加入的候补输入中
                  let newFunctionName = {'name': functionDetail.expression}
                  if(!_this.supplementationList.some(function (supplementation){
                    return supplementation.name === newFunctionName.name
                  })){ _this.supplementationList.splice(_this.supplementationList.length, 0, newFunctionName);}
                }
              }
            }
          }
        },
        getDomain(){
            let url = '/autotest/domainSign/list/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.domains = response.data;
            });
        },
        getModule(){
            // 获得项目中的所有接口模块
            let url = '/autotest/module/list/api/' + this.$store.state.projectId;
            this.$get(url, response =>{
                response.data.unshift({id: "0", name:"默认模块", label: "默认模块"});
                this.modules = response.data;
            });
        },
        getDetail(route){
            if (route.params.caseId){  // 编辑
                let url = "/autotest/case/detail/api/" + route.params.caseId;
                this.$get(url, response => {
                    let data = response.data;
                    if(data.moduleId==='0'){
                        data.moduleName = "默认模块";
                    }
                    if(data.environmentIds){
                        data.environmentIds = JSON.parse(data.environmentIds);
                    }
                    if(data.commonParam){
                        data.commonParam = JSON.parse(data.commonParam);
                    }
                    for(let i=0;i<data.caseApis.length;i++){
                        let caseApi = data.caseApis[i];
                        caseApi.edit = false;
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
                    if(route.params.type === "copy"){ //复用
                        data.id = "";
                    }
                    this.caseForm = data;
                    this.addSupplementationList('relation');
                });
            }
            if(route.name === "API用例新增"){
              // 将默认的模块分类修改为选中的模块分类
              if(route.params.moduleId && route.params.moduleName){
                this.caseForm.moduleId = route.params.moduleId;
                this.caseForm.moduleName = route.params.moduleName;
              }
            }
        },
        cancelAdd(){
          // 如果是通过其他页面跳转过来的，点击返回回到用例管理页面
          if (window.history.state) {
            this.$router.go(-1)
          } else {
            this.$router.push('/caseCenter/caseManage')
          }
        },
        saveAdd(){
            // 如果用例中存在值
            this.$refs["caseForm"].validate(valid => {
                if (valid) {
                    // 如果存在值
                    this.caseForm.projectId = this.$store.state.projectId;
                    for(let i=0; i<this.caseForm.caseApis.length; i++){
                        this.caseForm.caseApis[i].index = i+1;
                    }
                    // 请求接口保存内容
                    let url = '/autotest/case/save';
                    this.$post(url, this.caseForm, response =>{
                        if(response.status===0){
                          this.$message.success("保存成功");
                        }

                        // 如果是复用，新增过来的用例的话，点击保存返回到用例列表页面，如果是编辑的话，仍然停留在编辑页。
                        if(this.$route.params.type === 'copy'){
                          this.$router.push({path: '/caseCenter/caseManage'});
                        }
                        else if(this.$route.name === "API用例新增"){
                          this.$router.push({path: '/caseCenter/caseManage'});
                        }
                        else{
                          this.addSupplementationList('relation')
                        }
                    });
                }else{
                    return false;
                }
            });
        },
        debugCase(){
            // 用例调试
            // this.runForm.engineId = 'system';
            if(this.debugEnvironmentId !== ''){
              this.runForm.environmentId = this.debugEnvironmentId;
            }
            else if(this.caseForm.environmentIds.length > 0){
                this.runForm.environmentId = this.caseForm.environmentIds[0];
            }
            this.runForm.sourceType = "temp";
            this.runForm.sourceId = this.caseForm.id;
            this.runForm.sourceName = this.caseForm.name;
            this.runForm.taskType = "debug";
            this.runForm.projectId = this.$store.state.projectId;
            this.runForm.debugData = JSON.parse(JSON.stringify(this.caseForm));
            // 增加禁用逻辑
            for(let i=0 ; i<this.runForm.debugData.caseApis.length; i++){
                if(this.runForm.debugData.caseApis[i].isBan === true){
                  this.runForm.debugData.caseApis.splice(i,1);
                  i--;
              }
            }
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
            this.debugEnvironmentId = this.runForm.environmentId;
        },
        closeResult(){
            this.resultVisable = false;
        },
        getUseFunction(data){
          this.FunctionList = data
        },
        callbackUseFunctionDetail(data){
          // 获得子组件传递过来的所有的自定义函数信息
          this.functionListDetail = data
        },
        callbackUseParamDetail(data){
          // 获得子组件传递过来的所有的自定义函数信息
          this.paramListDetail = data
        },
        editCodeCharge(flag){
            // 判断前后置脚本是否为关闭状态
            this.isCodeEdit = flag === true;
        },
        changeInDesc(row){
            // 将编辑按钮置为开启状态
            this.$set(row, 'edit', true);

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
.font-size-12{
  font-size: 12px;
  text-align: center;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  padding: 0 5px;
  width: 55px;
}
</style>
