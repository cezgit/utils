require('dotenv').config()
import { MongoClient } from 'mongodb'

const connectMongoDB = () => MongoClient.connect(process.env.MONGODB)

const getContacts = (req, res) => {
  return connectMongoDB()
    .then(
      db => db.collection('contact')
        .find({})
        .toArray()
        .then(documents => ({db, documents}))
    )
    .then(({db, documents}) => {
      db.close()
      return documents
    })
    .then(contacts => res.json(contacts))
    .catch(err => res.status(400).send(err.toString()))
}

const createContact = (req, res) => {
  return connectMongoDB()
    .then(
      db => db.collection('contact').insertOne(req.body)
        .then(result => db)
    )
    .then(db => db.close())
    .then(() => res.json({result: 'ok'}))
    .catch(err => res.status(400).send(err.toString()))
}

export const handler = (req, res) => {

  res.set('Access-Control-Allow-Origin', "*")
  res.set('Access-Control-Allow-Methods', 'GET, POST')
  res.set('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept')

  if (req.method === 'POST') {
    return createContact(req, res)
  }
  return getContacts(req, res)
}

// export const helloHttp = (req, res) => res.send(`Hello ${req.body.name || 'World'}!`);

// exports.helloBackground = function helloBackground (event, callback) {
//   callback(null, `Hello ${event.data.name || 'World'}!`);
// };
