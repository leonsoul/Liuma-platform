/**
* 请求Rest参数
*/
<template>
    <div>
        <el-table :data="reqRest">
            <el-table-column label="参数名称" prop="name">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入参数名称" v-model="reqRest[scope.$index].name"/>
                </template>
            </el-table-column>
            <el-table-column label="参数值" prop="value">
                <template slot-scope="scope">
                  <el-autocomplete  size="small" style="width: 90%" placeholder="请输入参数值" v-model="reqRest[scope.$index].value"
                                    :fetch-suggestions="querySearch"  @select="handleSelect" :trigger-on-focus="false"/>
<!--                    <el-input size="small" style="width: 90%" placeholder="请输入参数值" v-model="reqRest[scope.$index].value"/>-->
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
  name: 'RequestRest',
  props:{
    reqRest:Array,
    supplementationList:Array,
  },
  methods: {
    add(){
        this.reqRest.push({name:"",value:""});
    },
    remove(index){
        this.reqRest.splice(index, 1);
    },
    deleteAll(){
        this.reqRest.splice(0, this.reqRest.length);
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
