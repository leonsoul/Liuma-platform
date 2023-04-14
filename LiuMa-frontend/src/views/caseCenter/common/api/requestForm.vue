/**
* 请求Form参数
*/
<template>
    <div>
        <el-table :data="reqForm">
            <el-table-column label="参数名称" prop="name">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入参数名称" v-model="reqForm[scope.$index].name"/>
                </template>
            </el-table-column>
            <el-table-column label="参数类型" prop="type" width="200px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 90%" v-model="reqForm[scope.$index].type">
                        <el-option v-for="item in types" :key="item" :label="item" :value="item"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="必填项" prop="required" width="200px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 90%" v-model="reqForm[scope.$index].required">
                        <el-option v-for="item in requires" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="参数内容" prop="value">
                <template slot-scope="scope">
                    <!--非文件时输入框 修改为带上识别的输入框，如果导入过自定义函数，就能输入{ 来自动生成识别-->
                    <el-autocomplete v-if="reqForm[scope.$index].type !== 'File'"  size="small" style="width: 90%" placeholder="请输入参数内容" v-model="reqForm[scope.$index].value"
                              :fetch-suggestions="querySearch"  @select="handleSelect" :trigger-on-focus="false"
                    />
                    <!-- 文件时选择框 -->
                    <el-select v-if="reqForm[scope.$index].type === 'File'"  size="small" style="width: 90%;" v-model="reqForm[scope.$index].value" filterable clearable placeholder="请选择文件">
                        <el-option v-for="item in files" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button size="small" type="text" @click="remove(scope.$index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button size="small" icon="el-icon-plus" type="text" @click="add">新增</el-button>
        <el-button size="small" type="text" @click="deleteAll">删除全部</el-button>
    </div>
</template>
<script>
export default {
  name: 'RequestForm',
  props:{
    reqForm: Array,
    caseForm: Object,
  },
  data() {
      return{
        requires: [
            {label: '必填', value: true},
            {label: '非必填', value: false},
        ],
        types: ["String", "Int", "Float", "Boolean", "JSONObject", "JSONArray", "File"],
        files:[],
        functionList:[]
      }
    },
    created() {
        this.getFiles();
        this.getFunction()
    },
    methods: {
        getFiles(){
            let url = '/autotest/file/all/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.files = response.data;
            });
        },
        add(){
            this.reqForm.push({name:"", value:"", type:"String", required: true});
        },
        remove(index){
            this.reqForm.splice(index, 1);
        },
        deleteAll(){
            this.reqForm.splice(0, this.reqForm.length);
        },
        getFunction(){
          let url = '/autotest/function/custom/list/' + this.$store.state.projectId;
          this.$get(url, response =>{
            // console.log(this.caseForm)
            if(this.caseForm){
              for(let item of this.caseForm.commonParam.functions){
                for(let functionItem of response.data){
                  if (functionItem.id === item)
                    this.functionList = this.functionList.concat(functionItem)
                }
              }
            }
          });
        },
      querySearch(queryString, cb) {
        let res = []
        let restaurants = this.functionList;
        let results = queryString ? restaurants.filter(this.createFilter(queryString)) : restaurants;
        // 将匹配后的结果修改为正确的格式返回
        for (let item of results){
          res =  res.concat({'value':item.expression})
        }
        // 调用 callback 返回建议列表的数据
        cb(res);
      },
      createFilter(queryString) {
        return (restaurant) => {
          return (restaurant.expression.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
        };
      },
      handleSelect(item) {
        console.log('handleSelect',item);
      },
    },


}
</script>
<style scoped>

</style>
