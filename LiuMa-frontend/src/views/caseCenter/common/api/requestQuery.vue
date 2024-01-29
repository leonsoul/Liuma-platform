/**
* 请求Query参数
*/
<template>
    <div>
        <el-table :data="reqQuery">
            <el-table-column label="参数名称" prop="name">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入参数名称" v-model="reqQuery[scope.$index].name"/>
                </template>
            </el-table-column>
            <el-table-column label="必填项" width="200px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 90%" v-model="reqQuery[scope.$index].required">
                        <el-option v-for="item in requires" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="参数值" prop="value">
                <template slot-scope="scope">
                  <!--非文件时输入框 修改为带上识别的输入框，如果导入过自定义函数，就能输入{ 来自动生成识别-->
                  <el-autocomplete  size="small" style="width: 90%" placeholder="请输入参数值" v-model="reqQuery[scope.$index].value"
                                   :fetch-suggestions="querySearch"  @select="handleSelect" :trigger-on-focus="false"/>
<!--                    <el-input size="small" style="width: 90%" placeholder="请输入参数值" v-model="reqQuery[scope.$index].value"/>-->
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
  name: 'RequestQuery',
  props:{
    reqQuery:Array,
    supplementationList:Array
  },
  data() {
      return{
        requires: [
            {label: '必填', value: true},
            {label: '非必填', value: false},
        ]
      }
  },
  methods: {
    add(){
        this.reqQuery.push({name:"", value:"", required: true});
    },
    remove(index){
        this.reqQuery.splice(index, 1);
    },
    deleteAll(){
        this.reqQuery.splice(0, this.reqQuery.length);
    },
    querySearch(queryString, cb) {
      let res = []
      let restaurants = this.supplementationList;

      let results = queryString ? restaurants.filter(this.createFilter(queryString)) : restaurants;
      // 将匹配后的结果修改为正确的格式返回
      for (let item of results){
        res =  res.concat({'value':item.name})
      }
      // 调用 callback 返回建议列表的数据
      cb(res);
    },
    createFilter(queryString) {

      return (restaurant) => {
        return (restaurant.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
      };
    },
    handleSelect(item) {
      // console.log('handleSelect',item);
    },
  }

}
</script>
<style scoped>

</style>
